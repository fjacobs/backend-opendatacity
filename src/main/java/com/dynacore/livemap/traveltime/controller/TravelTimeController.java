package com.dynacore.livemap.traveltime.controller;

import com.dynacore.livemap.core.FeatureRequest;
import com.dynacore.livemap.core.TrafficController;
import com.dynacore.livemap.core.model.TrafficDTO;
import com.dynacore.livemap.core.model.TrafficFeatureImpl;
import com.dynacore.livemap.traveltime.Mapper;
import com.dynacore.livemap.traveltime.domain.TravelTimeMapDTO;
import com.dynacore.livemap.traveltime.service.TravelTimeService;
import org.geojson.Feature;
import org.geojson.FeatureCollection;
import org.locationtech.jts.geom.LineString;
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
import java.util.Map;

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
  public Flux<Feature> streamLiveData() {
    logger.info("Enter TravelTimeGeoJsonController::streamLiveData");
    return service.getLiveData().map(TrafficFeatureImpl::getGenericGeoJson);
  }


  record RequestOptions(Long replayInterval, LatLngBounds currentBounds) {}

  @CrossOrigin(origins = "http://localhost:9000")
  @MessageMapping("TRAVELTIME_REPLAY")
  public Flux<List<TravelTimeMapDTO>> replayAllDistinct(Map<String, Object> params) {
    logger.info("Enter TravelTimeGeoJsonController::replayAllDistinct");
    return service
        .replayHistoryGroup(Duration.ofMillis( (Integer) params.get("replayInterval")))
        .doOnNext(dto -> System.out.println("sending:" + dto));
  }

  @Override
  public Flux<? extends List<? extends TrafficDTO>> replayAllDistinct(Integer intervalMilliSec) {
    return null;
  }



  /*  Returns Feature properties without geolocation
   */
  @Override
  @CrossOrigin(origins = "http://localhost:9000")
  @MessageMapping("TRAVELTIME_REPLAYV2")
  public Flux<List<TravelTimeMapDTO>> replayDistinctFeatures(FeatureRequest request) {
    logger.info("Enter TravelTimeGeoJsonController::replayDistinctFeatures");
    logger.info("Received requestOptions: " + request);
    return service
        .getFeatureRange(request.startDate(), request.direction(), Duration.ofMillis(request.intervalMilliSec()));
  }
  //  public Flux<TravelTimeRepo.IdLoc> getLocations(double yMin, double xMin, double yMax, double
  // xMax) {
  /*  Returns geojson FeatureCollection with locations
   */
  @CrossOrigin(origins = "http://localhost:9000")
  @MessageMapping("TRAVELTIME_INIT")
  public Mono<FeatureCollection> getFeatureCollection(LatLngBounds bounds) {
    logger.info("Enter TravelTimeGeoJsonController::getFeatureRange");
    return service
        .getLocationFlux(bounds.south(), bounds.west(), bounds.north(), bounds.east())
        .map(
            idLoc -> {
              LineString x = (LineString) idLoc.geom();
              org.geojson.LineString out = Mapper.lineStringConvertor(x);
              return Mapper.convertToGeojson(idLoc.id(), out);
            })
        .collectList()
        .map(
            features -> {
              FeatureCollection fc = new FeatureCollection();
              fc.setFeatures(features);
              return fc;
            })
        .doOnNext(features -> System.out.println(features.toString()));
  }
}
