package com.dynacore.livemap.core;

import com.dynacore.livemap.core.model.TrafficFeature;
import com.dynacore.livemap.core.model.TrafficDTO;
import com.dynacore.livemap.core.model.TrafficFeatureInterface;
import org.geojson.FeatureCollection;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface TrafficController {

    Flux<? extends TrafficFeatureInterface> streamLiveData();

    Flux< ? extends List<? extends TrafficDTO>> replayAllDistinct(Integer intervalMilliSec);

    Flux<? extends TrafficDTO> getFeatureRange(FeatureRequest request);

}
