package com.dynacore.livemap.traveltime.service;

import com.dynacore.livemap.core.service.DistinctUtil;
import com.dynacore.livemap.core.service.FeatureDistinct;
import com.dynacore.livemap.traveltime.domain.TravelTimeFeatureImpl;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.SynchronousSink;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

@Component
public class TravelTimeFeatureDistinct implements FeatureDistinct<TravelTimeFeatureImpl, TravelTimeFeatureImpl> {

    @Autowired
    ModelMapper modelMapper;

    Map<String, Integer> geoJsonStore = new HashMap<>();

    @Override
    public BiConsumer<? super TravelTimeFeatureImpl, SynchronousSink<TravelTimeFeatureImpl>> getFilter() {
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
