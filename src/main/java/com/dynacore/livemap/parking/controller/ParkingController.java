package com.dynacore.livemap.parking.controller;

import com.dynacore.livemap.core.FeatureRequest;
import com.dynacore.livemap.core.TrafficController;
import com.dynacore.livemap.core.model.TrafficFeature;
import com.dynacore.livemap.core.model.TrafficDTO;
import com.dynacore.livemap.parking.domain.ParkingDTO;
import com.dynacore.livemap.parking.service.ParkingReactorService;
import org.geojson.FeatureCollection;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.lang.reflect.Type;
import java.util.List;

@Profile("parking")
@Controller
public class ParkingController implements TrafficController {

  private final ParkingReactorService service;
  private final Logger logger = LoggerFactory.getLogger(ParkingController.class);
  private final Type listType = new TypeToken<List<ParkingDTO>>() {}.getType();

  @Autowired private ModelMapper modelMapper;

  public ParkingController(ParkingReactorService service) {
    this.service = service;
  }

  @Override
  @CrossOrigin(origins = "http://localhost:9000")
  @MessageMapping("PARKING_STREAM")
  public Flux<TrafficFeature> streamLiveData() {
    logger.info("Enter ParkingGeoJsonController::streamLiveData");
    return service.getLiveData();
  }

  @Override
  @CrossOrigin(origins = "http://localhost:9000")
  @MessageMapping("PARKING_REPLAY")
  public Flux<List<ParkingDTO>> replayAllDistinct(Integer intervalMilliSec) {
    logger.info("Enter ParkingGeoJsonController::replayAllDistinct");
    return service.replayHistoryGroup().map(featureList -> modelMapper.map(featureList, listType));
  }

  /*  Returns Feature properties without geolocation
   */
  @Override
  @CrossOrigin(origins = "http://localhost:9000")
  @MessageMapping("PARKING_HISTORY")
  public Flux<TrafficDTO> getFeatureRange(FeatureRequest request) {
    logger.info("Enter ParkingGeoJsonController::getFeatureRange");
    return service
        .getFeatureRange(request)
        .map(feature -> modelMapper.map(feature, ParkingDTO.class));
  }

  @Override
  @CrossOrigin(origins = "http://localhost:9000")
  @MessageMapping("PARKING_COLLECTION")
  public Mono<FeatureCollection> requestResponseFc() {
    logger.info("Enter ParkingGeoJsonController::requestResponseFc");
    return service.getFeatureCollection();
  }
}
