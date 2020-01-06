package com.dynacore.livemap.traveltime.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.geojson.FeatureCollection;
import reactor.core.publisher.Flux;

import java.time.Duration;

public interface OpenDataRetriever {

    Flux<FeatureCollection> requestSourceFc(Duration interval) throws JsonProcessingException;
}
