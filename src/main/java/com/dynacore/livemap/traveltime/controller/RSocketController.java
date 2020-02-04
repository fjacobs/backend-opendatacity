package com.dynacore.livemap.traveltime.controller;

import com.dynacore.livemap.traveltime.domain.RoadDTO;
import com.dynacore.livemap.traveltime.repo.TravelTimeEntity;
import com.dynacore.livemap.traveltime.service.TravelTimeService;
import com.dynacore.livemap.traveltime.service.filter.FeatureRequest;
import org.geojson.Feature;
import org.geojson.FeatureCollection;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Controller
public class RSocketController {

  private final TravelTimeService service;
  private final Logger logger = LoggerFactory.getLogger(RSocketController.class);

  @Autowired private ModelMapper modelMapper;

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
  public Flux<List<TravelTimeEntity>> replayAll(Integer intervalSeconds) {
    logger.info("Enter RSocketController::streamHistory");
    return service.replayGroupedByPubdate(intervalSeconds);
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

  @CrossOrigin(origins = "http://localhost:9000")
  @MessageMapping("traveltime_collection")
  public Mono<FeatureCollection> requestResponseFc() {
    logger.info("Enter RSocketController::requestFeatureCollection");
    return service.getFeatureCollection();
  }
}
