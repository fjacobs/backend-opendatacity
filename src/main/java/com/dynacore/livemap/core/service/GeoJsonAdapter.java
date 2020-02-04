package com.dynacore.livemap.core.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.geojson.FeatureCollection;
import reactor.core.publisher.Flux;

import java.time.Duration;

public interface GeoJsonAdapter {

  Flux<FeatureCollection> requestHotSourceFc(Duration interval) throws JsonProcessingException;
}
