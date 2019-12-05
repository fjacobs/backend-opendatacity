package com.dynacore.livemap.core;

import org.geojson.Feature;

import reactor.core.publisher.Flux;

/*
    Publisher for GeoJson objects.
 */

public interface ReactiveGeoJsonPublisher {
    Flux<Feature> getFeatures();
}
