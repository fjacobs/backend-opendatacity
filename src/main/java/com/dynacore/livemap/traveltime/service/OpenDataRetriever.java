package com.dynacore.livemap.traveltime.service;

import org.geojson.Feature;
import org.geojson.FeatureCollection;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface OpenDataRetriever {

    Flux<FeatureCollection> requestFeatures();
}
