package com.dynacore.livemap.core;

import org.geojson.FeatureCollection;
import org.springframework.http.codec.ServerSentEvent;
import reactor.core.publisher.Flux;

public interface ReactiveGeoJsonController {

  Flux<ServerSentEvent<FeatureCollection>> streamFeatureCollection();
}
