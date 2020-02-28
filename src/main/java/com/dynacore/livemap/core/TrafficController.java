package com.dynacore.livemap.core;

import com.dynacore.livemap.core.model.TrafficDTO;
import com.dynacore.livemap.core.model.TrafficFeature;
import org.geojson.Feature;
import reactor.core.publisher.Flux;

import java.util.List;

public interface TrafficController {

    Flux<? extends Feature> streamLiveData();

    Flux< ? extends List<? extends TrafficDTO>> replayAllDistinct(Integer intervalMilliSec);

  //  Flux< ? extends List<? extends TrafficDTO>> replayDistinctFeatures(FeatureRequest request);

}
