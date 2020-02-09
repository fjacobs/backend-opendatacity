package com.dynacore.livemap.traveltime.controller;

import com.dynacore.livemap.core.model.TrafficFeature;
import com.dynacore.livemap.core.service.GeoJsonReactorService;
import org.geojson.FeatureCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

/*
   Deprecated in favour of RSocket protocol
*/

@Profile("traveltime")
@RestController
public class HttpSseController {

  private final Logger logger = LoggerFactory.getLogger(HttpSseController.class);
  private GeoJsonReactorService travelTimeService;

  @Autowired
  public HttpSseController(GeoJsonReactorService TravelTimeService) {
    this.travelTimeService = TravelTimeService;
  }

  /**
   * @return Returns a subscription for individual features The first event contain all features,
   *     the events that follow only contains changed data
   */
  @CrossOrigin(origins = "http://localhost:8000")
  @GetMapping("/featureSubscription")
  public Flux<ServerSentEvent<TrafficFeature>> streamFeatures() {

    return travelTimeService
            .getLiveData()
            .doOnComplete(() -> logger.info("Completed Road SSE.."))
            .doOnError(e -> logger.error("SSE Error: " + e))
            .map(
                    sequence ->
                            ServerSentEvent.<TrafficFeature>builder()
                                    .id("Roads")
                                    .event("event")
                                    .data((TrafficFeature) sequence)
                                    .build());
  }
}
