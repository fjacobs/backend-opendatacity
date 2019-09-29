package com.dynacore.livemap.traveltime;

import com.dynacore.livemap.common.repo.JpaRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.geojson.Feature;
import org.geojson.FeatureCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

@Profile("traveltime")
@Service("travelTimeService")
public class TravelTimeService {

    private static final String ID = "Id";
    private static final String NAME = "Name";
    private static final String TYPE = "Type";
    private static final String TRAVEL_TIME = "Traveltime";
    private static final String LENGTH = "Length";
    private static final String VELOCITY = "Velocity";
    private static final String OUR_RETRIEVAL = "retrievedFromThirdParty";
    private static final String THEIR_RETRIEVAL = "Timestamp";
    private static final String DYNACORE_ERRORS = "dynacoreErrors";

    private Mono<FeatureCollection> fluxFeatureColl;
    private JpaRepository<TravelTimeEntity> travelTimeRepo;
    private TravelTimeConfiguration config;
    private WebClient webClient = WebClient.create();
    private Logger logger = LoggerFactory.getLogger(TravelTimeService.class);

    @Autowired
    public TravelTimeService(JpaRepository<TravelTimeEntity> travelTimeRepo, TravelTimeConfiguration config) {
        this.travelTimeRepo = travelTimeRepo;
        this.config = config;
//        try{
//            pollRequest();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }


    private Mono<FeatureCollection> getMonoFromGeojsonUrl(String url) {
        WebClient webClient = WebClient.create();
        return webClient.get()
                .uri(config.getUrl())
                .retrieve()
                .bodyToMono(byte[].class) // Data source doesn't include a content-type header,
                                             // so we need to convert the json from byte stream
                .map(bytes -> {
                    FeatureCollection fc = null;
                    try {
                        fc = new ObjectMapper().readValue(bytes, FeatureCollection.class);
                    } catch (IOException e) {
                        System.err.println("error: " + e);
                    }
                    System.out.println("FC1 size: " + fc.getFeatures().size()) ;
                    return fc;
                });
    }

    Flux<Feature> getFeatureFlux(String url) {
        return getMonoFromGeojsonUrl(url)
                .flatMapIterable(FeatureCollection::getFeatures)
                .cache()
                .parallel(8)
                .runOn(Schedulers.parallel())
                .doOnNext(this::processFeature)
                .sequential()
                .doOnComplete(() -> System.out.println("Completed: getFluxFromMonoGeoJson"))
                .doOnError(e -> logger.error("Error processing roads to flux:" + e));
    }

    //List<Feature> -> FeatureCollection
    FeatureCollection getFc() {
        FeatureCollection fc = new FeatureCollection();
        fc.setFeatures(getFeatureList(config.getUrl()));
        return fc;
    }


    // Flux<Feature>  ->  List<Feature>
    List<Feature> getFeatureList(String url) {
        Flux flux = getFeatureFlux(url);
        List<Feature> returnColl = (ArrayList<Feature>) flux.collectList().block();
        return returnColl;
    }








    private void pollRequest() {
        ScheduledExecutorService exec = Executors.newSingleThreadScheduledExecutor();
        fluxFeatureColl = webClient
                .get()
                .uri(config.getUrl())
                .retrieve()
                .bodyToMono(byte[].class) //Since the provider didn't add a header..
                .map(bytes -> {
                    FeatureCollection fc = null;
                    try {
                        fc = new ObjectMapper().readValue(bytes, FeatureCollection.class);
                    } catch (IOException e) {
                        System.err.println("Error converting bytestream to JSON: " + e);
                    }
                    return fc;
                });
    }

    Mono<FeatureCollection> getFluxFeatureColl() {
        return fluxFeatureColl;
    }


    private Feature processFeature(Feature feature) {
        String  retrieved = LocalDateTime.now().toString();

        feature.getProperties().put(DYNACORE_ERRORS, "none");
        feature.getProperties().put(OUR_RETRIEVAL, retrieved);
        if (!feature.getProperties().containsKey(TRAVEL_TIME)) {
            feature.getProperties().put(TRAVEL_TIME, -1);
        }
        if (!feature.getProperties().containsKey(VELOCITY)) {
            feature.getProperties().put(VELOCITY, -1);
        }
        if (!feature.getProperties().containsKey(LENGTH)) {
            feature.getProperties().put(LENGTH, -1);
        }
        return feature;
    }


    @Transactional
    public synchronized void saveCollection(FeatureCollection travelTimeFc) {
        try {
            travelTimeFc.getFeatures().forEach(travelTime -> travelTimeRepo.save(
                    new TravelTimeEntity.Builder()
                            .id((String) travelTime.getProperties().get(ID))
                            .name((String) travelTime.getProperties().get(NAME))
                            .pubDate((String) travelTime.getProperties().get(THEIR_RETRIEVAL))
                            .retrievedFromThirdParty((LocalDateTime) travelTime.getProperties().get(OUR_RETRIEVAL))
                            .type((String) travelTime.getProperties().get(TYPE))
                            .travelTime((int) travelTime.getProperties().get(TRAVEL_TIME))
                            .velocity((int) travelTime.getProperties().get(VELOCITY))
                            .length((int) travelTime.getProperties().get(LENGTH))
                            .build()));
        } catch (Exception error) {
            logger.error("Can't save road information to DB: " + error.toString());
        }
    }


}
