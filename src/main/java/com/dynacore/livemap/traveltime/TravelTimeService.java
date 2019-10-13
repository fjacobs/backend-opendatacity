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
import org.springframework.http.HttpStatus;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

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
//    private final HttpClientFactory httpClientFactory;
    private final TravelTimeConfiguration config;
    private Flux<Feature> featurePublisher;
    WebClient webClient;

    @Autowired
    public TravelTimeService(DatabaseClient databaseClient, HttpClientFactory httpClientFactory, TravelTimeConfiguration config) {
        this.databaseClient = databaseClient;

        ReactorClientHttpConnector httpConnector = new ReactorClientHttpConnector( httpClientFactory.autoConfigHttpClient(SOURCEURL));
        webClient= WebClient.builder().clientConnector(httpConnector)
                .build();

        this.config = config;

        featurePublisher = pollServer()
                .flatMap(Flux::fromIterable)
                .parallel(Runtime.getRuntime().availableProcessors())
                .runOn(Schedulers.parallel())
                .doOnNext(this::processFeature)
                .sequential()
                .doOnComplete(() -> logger.info("processed features"))
                .share()
                .cache();

        featurePublisher.subscribe();
//        //Subscriber 1/2
        Flux<Feature> saveSubscription = Flux.from(featurePublisher);
        saveSubscription
                .doOnNext(x -> logger.info("x"))
                .doOnNext(this::save)
                .doOnComplete(() -> logger.info("Save to db complete..."))
                .subscribe();
    }

    //Subscriber 2/2
    Flux<Feature> getPublisher() {
        return null;
    }



    Flux<Feature> getPublisher2() {
        return featurePublisher;
    }

    private Flux<FeatureCollection> pollServer() {
        return Flux.<FeatureCollection>generate(sink -> {
            sink.next(dataSourceSubscription());
        })
        .delayElements(Duration.ofSeconds(5))
        .log();
    }

    private FeatureCollection dataSourceSubscription() {
        FeatureCollection fc = webClient.get()
                .uri(SOURCEURL)
                .retrieve()
                .onStatus(HttpStatus::is3xxRedirection, clientResponse ->
                        Mono.error(new RuntimeException("Unmodified"))
                )                .bodyToMono(byte[].class)
                .delayElement(Duration.ofSeconds(3))

                // Data source doesn't include a content-type header: convert from bytes to json
                .map(bytes -> {
                    FeatureCollection featureColl = null;
                    if(bytes!=null) {
                        try {
                            featureColl = Optional.of(new ObjectMapper().readValue(bytes, FeatureCollection.class))
                                    .orElseThrow();
                        } catch (IOException e) {
                            logger.error("Error: " + e);
                            featureColl = new FeatureCollection();
                        }
                        return featureColl;
                    } else {
                        featureColl = new FeatureCollection();
                    }
                    return featureColl;

                })
                .retryBackoff(1200,Duration.ofSeconds(2))
                .doOnSuccess(x -> logger.info("Serialized Mono<FeatureCollection> from datasource"))
                .doOnError((e)-> logger.error("error--> "+ e))
                .block();
        return fc;
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

    @Transactional
    public void save(Feature travelTime) {
        try {
            databaseClient.insert()
                    .into(TravelTimeEntity.class)
                    .using(new TravelTimeEntity.Builder()
                            .id((String) travelTime.getProperties().get(ID))
                            .name((String) travelTime.getProperties().get(NAME))
                            .pubDate((String) travelTime.getProperties().get(THEIR_RETRIEVAL))
                            .retrievedFromThirdParty((String) travelTime.getProperties().get(OUR_RETRIEVAL))
                            .type((String) travelTime.getProperties().get(TYPE))
                            .travelTime((int) travelTime.getProperties().get(TRAVEL_TIME))
                            .velocity((int) travelTime.getProperties().get(VELOCITY))
                            .length((int) travelTime.getProperties().get(LENGTH))
                            .build())
                    .then()
                    .subscribe();


        } catch (Exception error) {
            logger.error("Can't save road information to DB: " + error.toString());
        }
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
        String retrieved = LocalDateTime.now().toString();
        feature.getProperties().put(DYNACORE_ERRORS, "none"); //TODO: (3) Implement error handling
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
