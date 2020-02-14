package com.dynacore.livemap.core;

import com.dynacore.livemap.core.model.TrafficDTO;
import com.dynacore.livemap.core.model.TrafficFeature;
import reactor.core.publisher.Flux;

import java.util.List;

public interface TrafficController {

    Flux<? extends TrafficFeature> streamLiveData();

    Flux< ? extends List<? extends TrafficDTO>> replayAllDistinct(Integer intervalMilliSec);

    Flux<? extends TrafficDTO> getFeatureRange(FeatureRequest request);

}
