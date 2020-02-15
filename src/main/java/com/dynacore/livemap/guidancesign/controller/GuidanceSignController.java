package com.dynacore.livemap.guidancesign.controller;

import com.dynacore.livemap.core.FeatureRequest;
import com.dynacore.livemap.core.TrafficController;
import com.dynacore.livemap.guidancesign.domain.GuidanceSignFeatureImpl;
import com.dynacore.livemap.guidancesign.service.DisplayDTO;
import com.dynacore.livemap.guidancesign.service.GuidanceSignService;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.List;

@Profile("traveltime")
@Controller
public class GuidanceSignController implements TrafficController {

  private final GuidanceSignService service;
  private final Logger logger = LoggerFactory.getLogger(GuidanceSignController.class);

  @Autowired private ModelMapper modelMapper;

  public GuidanceSignController(GuidanceSignService service) {
    this.service = service;
  }

  @Override
  @CrossOrigin(origins = "http://localhost:9000")
  @MessageMapping("GUIDANCESIGN_STREAM")
  public Flux<GuidanceSignFeatureImpl> streamLiveData() {
    logger.info("Enter GuidanceSignGeoJsonController::streamLiveData");
    return service.getLiveData();
  }

  @Override
  @CrossOrigin(origins = "http://localhost:9000")
  @MessageMapping("GUIDANCESIGN_REPLAY")
  public Flux<List<DisplayDTO>> replayAllDistinct(Integer intervalMilliSec) {
    logger.info("Enter GuidanceSignGeoJsonController::replayAllDistinct");
    return service.replayHistoryGroup(Duration.ofMillis(intervalMilliSec));
  }

  /*  Returns Feature properties without geolocation
   */
  @Override
  @CrossOrigin(origins = "http://localhost:9000")
  @MessageMapping("GUIDANCESIGN_HISTORY")
  public Flux<DisplayDTO> getFeatureRange(FeatureRequest request) {
    logger.info("Enter GuidanceSignJsonController::getFeatureRange");
    return service.getFeatureRange(request).map(feature-> modelMapper.map(feature, DisplayDTO.class));

  }
}
