package com.dynacore.livemap.traveltime.controller;

import com.dynacore.livemap.core.geojson.TrafficFeature;
import com.dynacore.livemap.core.model.TrafficDTO;
import com.dynacore.livemap.traveltime.service.filter.FeatureRequest;
import org.geojson.FeatureCollection;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface GeoJsonController {

    Flux<TrafficFeature> streamLiveData();

    Flux< ? extends List<? extends TrafficDTO>> replayAllDistinct(Integer intervalMilliSec);

    Flux<? extends TrafficDTO> getFeatureRange(FeatureRequest request);

    Mono<FeatureCollection> requestResponseFc();
}
