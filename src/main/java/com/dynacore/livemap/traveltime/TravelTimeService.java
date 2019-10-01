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
import reactor.util.function.Tuple2;

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
    private final Logger logger = LoggerFactory.getLogger(TravelTimeService.class);
    private static final String SOURCEURL = "http://web.redant.net/~amsterdam/ndw/data/reistijdenAmsterdam.geojson";
    private static final int INTERVAL = 60;

    private JpaRepository<TravelTimeEntity> travelTimeRepo;
    private TravelTimeConfiguration config;

    @Autowired
    public TravelTimeService(JpaRepository<TravelTimeEntity> travelTimeRepo, TravelTimeConfiguration config) {
        this.travelTimeRepo = travelTimeRepo;
        this.config = config;
    }

    Flux<FeatureCollection> scheduleExchange() {
        Mono<FeatureCollection> monoFc = processFeatures(getData())
                .collectList()
                .map(features -> {
                    FeatureCollection fc = new FeatureCollection();
                    fc.setFeatures(features);
                    return fc;
                }).log();
                                                                // need a flux for scheduling
        Flux<FeatureCollection> scheduleReady = Flux.concat(monoFc); // create flux with one element
        Flux<Long> interval = Flux.interval(Duration.ofSeconds(INTERVAL));

        return Flux.zip(interval, scheduleReady)
                   .map(Tuple2::getT2);
    }

    Flux<Feature> processFeatures(Mono<FeatureCollection> sourceColl) {
        return sourceColl
                .flatMapIterable(FeatureCollection::getFeatures)
                .parallel(Runtime.getRuntime().availableProcessors())
                .runOn(Schedulers.parallel())
                .doOnNext(this::processFeature)
                .sequential();
    }

    //OUD
    Mono<FeatureCollection> getData() {
        WebClient webClient = WebClient.create();
        return webClient.get()
                .uri(SOURCEURL)
                .retrieve()
                .bodyToMono(byte[].class) // Data source doesn't include a content-type header: convert from bytes to json
                .doOnSuccess(x -> logger.info("Serialized Mono<FeatureCollection> from datasource"))
                .map(bytes -> {
                    FeatureCollection fc = null;
                    try {
                        fc = Optional.of(new ObjectMapper().readValue(bytes, FeatureCollection.class))
                                .orElseThrow();
                    } catch (IOException e) {
                        logger.error("Error: " + e);
                        fc = new FeatureCollection();
                    }
                    return fc;
                });
    }

    /**
     * Publishes changed properties to subscribers.
     * To keep the class stateless, we use the last stored DB entry to store the data
     */
    public Flux<Feature> updateFlux() {
        Flux<Long> interval = Flux.interval(Duration.ofSeconds(1));
        return Flux.zip(interval, processFeatures(getData())).map(Tuple2::getT2);
    }

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

    @Transactional
    public synchronized void saveCollection(FeatureCollection travelTimeFc) { // TODO: (1) Implement async database
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
