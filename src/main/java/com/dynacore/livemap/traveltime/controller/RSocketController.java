package com.dynacore.livemap.traveltime.controller;


import org.geojson.Feature;
import org.geojson.FeatureCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

import com.dynacore.livemap.traveltime.service.TravelTimeService;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Controller
public class RSocketController {

    private final TravelTimeService service;
    private final Logger logger = LoggerFactory.getLogger(RSocketController.class);

    public RSocketController(TravelTimeService service) {
        this.service = service;
    }

    //RSocket request-response mode
    @MessageMapping("traveltime-collection")
    public Mono<FeatureCollection> streamFeatureCollection() {
        logger.info("Enter RSocketController::streamFeatureCollection");
        return service.getFeatureCollection();
    }

    //RSocket request-stream mode
    @MessageMapping("traveltime-message")
    public Flux<Feature> streamFeatures() {
        logger.info("Enter RSocketController::streamFeatures");
        return service.getFeatures();
    }
}
