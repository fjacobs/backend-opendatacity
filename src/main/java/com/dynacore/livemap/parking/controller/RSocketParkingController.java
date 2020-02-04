package com.dynacore.livemap.parking.controller;


import com.dynacore.livemap.parking.service.ParkingService;
import com.dynacore.livemap.traveltime.domain.RoadDTO;
import com.dynacore.livemap.traveltime.repo.TravelTimeEntity;
import com.dynacore.livemap.traveltime.service.filter.FeatureRequest;
import org.geojson.Feature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import reactor.core.publisher.Flux;

import java.util.List;

@Controller
public class RSocketParkingController {

    private final ParkingService service;
    private final Logger logger = LoggerFactory.getLogger(RSocketParkingController.class);


    public RSocketParkingController(ParkingService service) {
        this.service = service;
    }

    @CrossOrigin(origins = "http://localhost:9000")
    @MessageMapping("PARKING_STREAM")
    public Flux<Feature> streamLiveData() {
        logger.info("Enter RSocketController::streamFeatures");
        return service.getLiveData();
    }

    @CrossOrigin(origins = "http://localhost:9000")
    @MessageMapping("TRAVELTIME_REPLAY_UNIQUE")
    public Flux<List<RoadDTO>> replayAllDistinct(Integer intervalMilliSec) {
        logger.info("Enter RSocketController::streamHistory");
        return service.replayGroupedByPubdateUnique(intervalMilliSec);
    }


    @CrossOrigin(origins = "http://localhost:9000")
    @MessageMapping("TRAVELTIME_REPLAY_MINIMAL")
    public Flux<List<RoadDTO>> replayAllDistinctMinimal(Integer intervalMilliSec) {
        logger.info("Enter RSocketController::streamHistory");
        return service.replayGroupedByPubdateUniqueMinimal(intervalMilliSec);
    }

    /*  Returns Feature properties without geolocation
    */
    @CrossOrigin(origins = "http://localhost:9000")
    @MessageMapping("TRAVELTIME_HISTORY")
    public Flux<TravelTimeEntity> getEntityRange(FeatureRequest request) {
        logger.info("Enter RSocketController::streamHistory");
        return service.getFeatureRange(request);
    }

}
