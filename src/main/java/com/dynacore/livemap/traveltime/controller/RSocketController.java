package com.dynacore.livemap.traveltime.controller;


import com.dynacore.livemap.traveltime.repo.ReplayMetaData;
import com.dynacore.livemap.traveltime.repo.TravelTimeEntity;
import com.dynacore.livemap.traveltime.service.TravelTimeService;
import com.dynacore.livemap.traveltime.service.filter.DateRangeRequest;
import org.geojson.Feature;
import org.geojson.FeatureCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

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

    /*
      Call to determine future requests for Feature history streaming replay.
        - The Feature count can determine the request(n) size for every pub_date.
        - The OffsetDateTime between multiple pub_dates to calculate the fast forward or rewind speed
        and the time interval between every request(n)
    */
    @CrossOrigin(origins = "http://localhost:9000")
    @MessageMapping("TRAVELTIME_REPLAY_METADATA")
    public Flux<ReplayMetaData> replayMetaData() {
        logger.info("Enter RSocketController::streamHistory");
        return service.getReplayMetaData();
    }


    /*  Returns Feature properties without geolocation
    */
    @CrossOrigin(origins = "http://localhost:9000")
    @MessageMapping("TRAVELTIME_HISTORY")
    public Flux<TravelTimeEntity> getEntityRange(DateRangeRequest request) {
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
