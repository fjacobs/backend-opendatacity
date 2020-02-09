package com.dynacore.livemap.core.service;

import com.dynacore.livemap.core.model.TrafficFeatureInterface;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.SynchronousSink;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

@Component
public class TrafficFeatureDistinct implements FeatureDistinct<TrafficFeatureInterface, TrafficFeatureInterface > {

    @Autowired
    ModelMapper modelMapper;

    Map<String, Integer> geoJsonStore = new HashMap<>();

    @Override
    public BiConsumer<TrafficFeatureInterface, SynchronousSink<TrafficFeatureInterface>> getFilter() {
        return (feature, sink) -> {
            Integer newHash = DistinctUtil.hashCodeNoRetDate.apply(feature);
            Integer oldHash;

            if ((oldHash = geoJsonStore.get(feature.getId())) != null) {
                if (!newHash.equals(oldHash)) {
                    geoJsonStore.put(feature.getId(), newHash);
                    sink.next(feature);
                }

            } else {
                geoJsonStore.put(feature.getId(), newHash);
                sink.next(feature);
            }
        };
    }
}
