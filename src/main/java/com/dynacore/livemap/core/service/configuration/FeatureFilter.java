package com.dynacore.livemap.core.service.configuration;

import com.dynacore.livemap.core.model.TrafficFeature;
import com.dynacore.livemap.core.service.DistinctUtil;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.SynchronousSink;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

@Component
public class FeatureFilter implements Filter<TrafficFeature,TrafficFeature> {

    @Autowired
    ModelMapper modelMapper;

    Map<String, Integer> geoJsonStore = new HashMap<>();

    @Override
    public BiConsumer<? super TrafficFeature, SynchronousSink<TrafficFeature>> filter() {
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
