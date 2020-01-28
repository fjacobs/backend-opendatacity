package com.dynacore.livemap.traveltime.controller;


import com.dynacore.livemap.traveltime.service.filter.PubDateSizeResponse;
import com.dynacore.livemap.traveltime.repo.TravelTimeEntity;
import com.dynacore.livemap.traveltime.service.TravelTimeService;
import com.dynacore.livemap.traveltime.service.filter.FeatureRequest;
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
import java.util.List;

@Controller
public class RSocketController {

    private final TravelTimeService service;
    private final Logger logger = LoggerFactory.getLogger(RSocketController.class);

    public RSocketController(TravelTimeService service) {
        this.service = service;
    }

    @CrossOrigin(origins = "http://localhost:9000")
    @MessageMapping("TRAVELTIME_STREAM")
    public Flux<Feature> streamLiveData() {
        logger.info("Enter RSocketController::streamFeatures");
        return service.getLiveData();
    }

    @CrossOrigin(origins = "http://localhost:9000")
    @MessageMapping("TRAVELTIME_REPLAY")
    public Flux<List<TravelTimeEntity>> replayAll(Duration delay) {
        logger.info("Enter RSocketController::streamHistory");
        return service.replayGroupedByPubdate(delay);
    }

    /*  Returns Feature properties without geolocation
    */
    @CrossOrigin(origins = "http://localhost:9000")
    @MessageMapping("TRAVELTIME_HISTORY")
    public Flux<TravelTimeEntity> getEntityRange(FeatureRequest request) {
        logger.info("Enter RSocketController::streamHistory");
        return service.getFeatureRange(request);
    }

    //RSocket request-response mode
    @CrossOrigin(origins = "http://localhost:9000")
    @MessageMapping("traveltime_collection")
    public Mono<FeatureCollection> requestResponseFc() {
        logger.info("Enter RSocketController::requestFeatureCollection");
        return service.getFeatureCollection();
    }
}
