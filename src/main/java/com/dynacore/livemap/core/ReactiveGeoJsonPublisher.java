package com.dynacore.livemap.core;

import org.geojson.Feature;
import org.geojson.FeatureCollection;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


/*
    Publisher for GeoJson objects.
 */

public interface ReactiveGeoJsonPublisher {
    public Mono<FeatureCollection> getFeatureCollection(Flux<Feature> featureFlux);
    public Flux<Feature> getFeatures();
}
