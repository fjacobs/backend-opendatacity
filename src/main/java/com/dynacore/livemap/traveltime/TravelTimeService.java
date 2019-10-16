package com.dynacore.livemap.traveltime;

import com.dynacore.livemap.common.http.HttpClientFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.geojson.Feature;
import org.geojson.FeatureCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.data.r2dbc.core.DatabaseClient;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.ConnectableFlux;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.Optional;

import static org.springframework.data.r2dbc.query.Criteria.where;
import static org.springframework.http.HttpStatus.NOT_MODIFIED;

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
    private static final String SOURCEURL = "http://web.redant.net/~amsterdam/ndw/data/reistijdenAmsterdam.geojson";
    private static final int INTERVAL = 5;
    private final Logger logger = LoggerFactory.getLogger(TravelTimeService.class);
    private DatabaseClient databaseClient;
    private ConnectableFlux<Feature> sharedFlux;
    private WebClient webClient;


    @Autowired
    public TravelTimeService(DatabaseClient databaseClient, HttpClientFactory httpClientFactory) {
        this.databaseClient = databaseClient;

        ReactorClientHttpConnector httpConnector = new ReactorClientHttpConnector(httpClientFactory.autoConfigHttpClient(SOURCEURL));
        webClient = WebClient.builder().clientConnector(httpConnector)
                .build();

        sharedFlux = retrieveProcessedFeatures().publish();

        var saveFlux = Flux.from(sharedFlux)
                .parallel(Runtime.getRuntime().availableProcessors())
                .runOn(Schedulers.parallel())
                .map(this::convertToEntity)
                .doOnNext(this::save)
                .sequential();

        Flux.interval(Duration.ofSeconds(INTERVAL))
                .map(tick -> {
                    saveFlux.subscribe(); //x -> System.out.println("SaveFlux subscribed")
                    sharedFlux.connect();
                    logger.info("Interval count: " + tick);
                    return tick;
                }).subscribe();
    }

    private Mono<Boolean> selectFeature(TravelTimeEntity entity) {
        return databaseClient.select().from(TravelTimeEntity.class)
                .matching(where("id")
                        .is(entity.getId())
                        .and("pub_date")
                        .is(entity.getPub_date()))
                .fetch()
                .first()
                .hasElement();
    }

    private TravelTimeEntity convertToEntity(Feature travelTime) {
        TravelTimeEntity entity = new TravelTimeEntity.Builder()
                .id((String) travelTime.getProperties().get(ID))
                .name((String) travelTime.getProperties().get(NAME))
                .pubDate((String) travelTime.getProperties().get(THEIR_RETRIEVAL))
                .retrievedFromThirdParty((String) travelTime.getProperties().get(OUR_RETRIEVAL))
                .type((String) travelTime.getProperties().get(TYPE))
                .travelTime((int) travelTime.getProperties().get(TRAVEL_TIME))
                .velocity((int) travelTime.getProperties().get(VELOCITY))
                .length((int) travelTime.getProperties().get(LENGTH)).build();
        return entity;
    }

    @Transactional
    public void save(TravelTimeEntity entity) {
        try {
            selectFeature(entity)
                    .subscribe(foundInDb -> {
                        if (!foundInDb) {
                            databaseClient.insert()
                                    .into(TravelTimeEntity.class)
                                    .using(entity)
                                    .then()
                                    .doOnError(e -> logger.error("Error already exists:  ", e))
                                    .subscribe();
                        }
                    });
        } catch (Exception error) {
            logger.error("Can't save road information to DB: " + error.toString());
        }
    }

    Flux<Feature> getPublisher() {
        return sharedFlux;
    }

    private Flux<Feature> retrieveProcessedFeatures() {
        return webClient.get()
                .uri(SOURCEURL)
                .exchange()
                .filter(clientResponse -> (clientResponse.statusCode() != NOT_MODIFIED))
                .flatMap(clientResponse -> clientResponse.bodyToMono(byte[].class))
                .map(bytes -> {
                            FeatureCollection featureColl = null;
                            try {
                                featureColl = Optional.of(new ObjectMapper().readValue(bytes, FeatureCollection.class))
                                        .orElseThrow(IllegalStateException::new);
                            } catch (Exception e) {
                                return Mono.error(IllegalStateException::new);
                            }
                            return featureColl;
                        }
                )
                .cast(FeatureCollection.class)
                .flatMapMany(this::processFeatures);
    }

    private Flux<Feature> processFeatures(FeatureCollection fc) {
        return Flux.fromIterable(fc)
                .parallel(Runtime.getRuntime().availableProcessors())
                .runOn(Schedulers.parallel())
                .doOnNext(this::processFeature)
                .sequential()
                .share()
                .doOnSubscribe(s -> logger.info("Subscribed to processed features source"))
                .doOnComplete(() -> logger.info("Completed processing features"));

    }

    Mono<FeatureCollection> convertToFeatureCollection(Flux<Feature> featureFlux) {
        Mono<FeatureCollection> mono = featureFlux.collectList()
                .map(features -> {
                    FeatureCollection fc = new FeatureCollection();
                    fc.setFeatures(features);
                    return fc;
                });
        return mono;
    }


    /**
     * Publishes changed properties to subscribers.
     * To keep the class stateless, we use the last stored DB entry to store the data
     */
//    private Flux<Feature> updateFlux() {
//        Flux<Long> interval = Flux.interval(Duration.ofSeconds(1));
//        return Flux.zip(interval, doWork(unprocessedFeatures())).map(Tuple2::getT2);
//    }
    private Feature processFeature(Feature feature) {
        String retrieved = OffsetDateTime.now().toString();
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

}
