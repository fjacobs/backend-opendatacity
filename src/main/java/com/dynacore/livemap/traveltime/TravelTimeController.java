package com.dynacore.livemap.traveltime;

import org.geojson.Feature;
import org.geojson.FeatureCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.time.Duration;

@Profile("traveltime")
@RestController
public class TravelTimeController {

    private final Logger logger = LoggerFactory.getLogger(TravelTimeController.class);
    private TravelTimeService travelTimeService;

    @Autowired
    public TravelTimeController(TravelTimeService TravelTimeService) {
        this.travelTimeService = TravelTimeService;
    }

    /**
     * @return Returns a SSE subscription for the complete FeatureCollection.
     * The first event will send the complete collection, the events that follow
     *  only contain property data that has been changed compared to the previous event.
     */
    @CrossOrigin
    @GetMapping("/roadSubscription")
    public Flux<ServerSentEvent<FeatureCollection>> streamFeatureCollection() {
        Flux<Feature> collection = travelTimeService.getFeatures();
        Flux<FeatureCollection> featureColl = Flux.concat(travelTimeService.getFeatureCollection(collection));

        return featureColl
                .doOnComplete(()-> logger.info("Completed Road FeatureCollection standardSubscription.."))
                .doOnError(e ->  logger.error("SSE Error: " + e))
                .map(sequence -> ServerSentEvent.<FeatureCollection>builder()
                        .id("Roads")
                        .event("event")
                        .data(sequence)
                        .build());
    }

    /**
     * @return Returns a subscription for specific features
     * The events only contain data that has changed
     */
    @CrossOrigin(origins = "http://localhost:8000")
    @GetMapping("/featureSubscription")
    public Flux<ServerSentEvent<Feature>> streamFeatures() {
        return travelTimeService.getFeatures()
                .delayElements(Duration.ofMillis(5))
                .doOnNext(feature -> logger.info((String) feature.getProperties().get("Id")))
                .doOnComplete(()-> logger.info("Completed Road SSE.."))
                .doOnError(e ->  logger.error("SSE Error: " + e))
                .map(sequence -> ServerSentEvent.<Feature>builder()
                        .id("Roads")
                        .event("event")
                        .data(sequence)
                        .build());
    }


}
