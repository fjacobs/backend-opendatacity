package com.dynacore.livemap.traveltime.controller;

import com.dynacore.livemap.core.FeatureRequest;
import com.dynacore.livemap.core.TrafficController;
import com.dynacore.livemap.traveltime.domain.TravelTimeDTO;
import com.dynacore.livemap.traveltime.domain.TravelTimeFeature;
import com.dynacore.livemap.traveltime.service.TravelTimeService;
import org.geojson.FeatureCollection;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;

@Profile("traveltime")
@Controller
public class TravelTimeController implements TrafficController {

  private final TravelTimeService service;
  private final Logger logger = LoggerFactory.getLogger(TravelTimeController.class);

  @Autowired private ModelMapper modelMapper;

  public TravelTimeController(TravelTimeService service) {
    this.service = service;
  }

  @Override
  @CrossOrigin(origins = "http://localhost:9000")
  @MessageMapping("TRAVELTIME_STREAM")
  public Flux<TravelTimeFeature> streamLiveData() {
    logger.info("Enter TravelTimeGeoJsonController::streamLiveData");
    return service.getLiveData();
  }

  @Override
  @CrossOrigin(origins = "http://localhost:9000")
  @MessageMapping("TRAVELTIME_REPLAY")
  public Flux<List<TravelTimeDTO>> replayAllDistinct(Integer intervalMilliSec) {
    logger.info("Enter TravelTimeGeoJsonController::replayAllDistinct");
    return service.replayHistoryGroup(Duration.ofMillis(intervalMilliSec));
  }

  /*  Returns Feature properties without geolocation
   */
  @Override
  @CrossOrigin(origins = "http://localhost:9000")
  @MessageMapping("TRAVELTIME_HISTORY")
  public Flux<TravelTimeDTO> getFeatureRange(FeatureRequest request) {
    logger.info("Enter TravelTimeGeoJsonController::getFeatureRange");
    return service.getFeatureRange(request).map(feature-> modelMapper.map(feature, TravelTimeDTO.class));

  }

  @Override
  @CrossOrigin(origins = "http://localhost:9000")
  @MessageMapping("traveltime_collection")
  public Mono<FeatureCollection> requestResponseFc() {
    logger.info("Enter TravelTimeGeoJsonController::requestResponseFc");
    return service.getFeatureCollection();
  }
}
