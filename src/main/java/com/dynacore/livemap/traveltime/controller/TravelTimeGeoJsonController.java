package com.dynacore.livemap.traveltime.controller;

import com.dynacore.livemap.core.model.TrafficDTO;
import com.dynacore.livemap.core.geojson.TrafficFeature;
import com.dynacore.livemap.traveltime.domain.TravelTimeDTO;
import org.geojson.FeatureCollection;
import com.dynacore.livemap.traveltime.service.TravelTimeReactorService;
import com.dynacore.livemap.traveltime.service.filter.FeatureRequest;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.lang.reflect.Type;
import java.util.List;

@Controller
public class TravelTimeGeoJsonController implements GeoJsonController {

  private final TravelTimeReactorService service;
  private final Logger logger = LoggerFactory.getLogger(TravelTimeGeoJsonController.class);
  private final Type listType = new TypeToken<List<TravelTimeDTO>>() {}.getType();

  @Autowired private ModelMapper modelMapper;

  public TravelTimeGeoJsonController(TravelTimeReactorService service) {
    this.service = service;
  }

  @Override
  @CrossOrigin(origins = "http://localhost:9000")
  @MessageMapping("TRAVELTIME_STREAM")
  public Flux<TrafficFeature> streamLiveData() {
    logger.info("Enter TravelTimeGeoJsonController::streamLiveData");
    return service.getLiveData();
  }

  @Override
  @CrossOrigin(origins = "http://localhost:9000")
  @MessageMapping("TRAVELTIME_REPLAY")
  public Flux<List<TravelTimeDTO>> replayAllDistinct(Integer intervalMilliSec) {
    logger.info("Enter TravelTimeGeoJsonController::replayAllDistinct");
    return service.replayHistoryGroup().map(featureList-> modelMapper.map(featureList, listType));
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
