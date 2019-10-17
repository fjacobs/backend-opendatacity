package com.dynacore.livemap.common.tools;


import com.dynacore.livemap.traveltime.TravelTimeController;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.geojson.Feature;
import org.geojson.FeatureCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.ConnectableFlux;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Profile("test")
@RestController
public class FcTester {

    private final Logger logger = LoggerFactory.getLogger(FcTester.class);
    int timer = 0;

    public FcTester() {
    }


    @CrossOrigin
    @GetMapping(value = "getRoadSingle")
    public FeatureCollection getRoadSingle() {
        return getFeatureColl(1);
    }


    @CrossOrigin
    @GetMapping("/roadSubscriptionTest")
    public Flux<ServerSentEvent<FeatureCollection>> streamFeatureCollection() {
        logger.error("Enter streamFeatureCollection");
        ConnectableFlux<FeatureCollection> fc = Flux.fromIterable(getFeatureColl(0))
                                                 //   .distinctUntilChanged(Feature::hashCode)
                                                    .distinct()
                                                    .collectList()
                                                    .map(features -> {
                                                        FeatureCollection collection = new FeatureCollection();
                                                        collection.setFeatures(features);
                                                        return collection;
                                                    })
                                                    .flux()
                                                    .take(1)
                                                    .publish();


        Flux.interval(Duration.ofSeconds(10))
                .publishOn(Schedulers.single())
                .map(tick -> {
                    fc.connect();
                    logger.info("Interval count: " + tick);
                    return tick;
                }).subscribe();


        return fc
                .doOnError(e -> logger.error("Roads SSE Error: " + e))
                .doOnSubscribe(x -> logger.info("Roads SSE doOnSubscribe"))
                .doOnRequest(x -> logger.info("Roads SSE doOnRequest"))
                .doOnComplete(() -> logger.info("Roads SSE doOnComplete"))
                .map(sequence -> ServerSentEvent.<FeatureCollection>builder()
                        .id("Roads")
                        .event("event")
                        .data(sequence)
                        .build());
    }


    ///---------------------------------------------

    public FeatureCollection getFeatureColl(int i) {
//      Mono<FeatureCollection> test = Mono.empty();
        if ((i == 0) || (i ==3)) i = 1; else i++;

        FeatureCollection featureColl = null;
        try {

            byte[] content = Files.readAllBytes(Paths.get("featurecollection" + i + ".json"));
            logger.info("Loaded featurecollection" + i + ".json");

            ObjectMapper objectMapper = new ObjectMapper();
            featureColl = objectMapper.readValue(content, FeatureCollection.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Assert.notNull(featureColl, "Error: did not load file: ");
        return featureColl;
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

}