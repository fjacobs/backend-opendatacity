package com.dynacore.livemap.traveltime.controller;


import com.dynacore.livemap.traveltime.service.TravelTimeService;
import org.geojson.Feature;
import org.geojson.FeatureCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Controller
public class RSocketController {

    private final TravelTimeService service;
    private final Logger logger = LoggerFactory.getLogger(RSocketController.class);

    public RSocketController(TravelTimeService service) {
        this.service = service;
    }

    //RSocket request-stream mode
    //Get live data directly from the open data source
    @CrossOrigin(origins = "http://localhost:9000")
    @MessageMapping("TRAVELTIME_STREAM")
    public Flux<Feature> streamLiveData() {
        logger.info("Enter RSocketController::streamFeatures");
        return service.getLiveData();
    }

    //RSocket request-stream mode
    //Get road data from the database
    @CrossOrigin(origins = "http://localhost:9000")
    @MessageMapping("TRAVELTIME_HISTORY")
    public Flux<Feature> streamHistory() {
        logger.info("Enter RSocketController::streamHistory");
        return service.streamHistory().delayElements(Duration.ofMillis(1));
    }

    //RSocket request-response mode
    @CrossOrigin(origins = "http://localhost:9000")
    @MessageMapping("traveltime_collection")
    public Mono<FeatureCollection> requestResponseFc() {
        logger.info("Enter RSocketController::requestFeatureCollection");
        return service.getFeatureCollection();
    }



}
