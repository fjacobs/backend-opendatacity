package com.dynacore.livemap.guidancesign.service;

import com.dynacore.livemap.core.service.DistinctUtil;
import com.dynacore.livemap.core.service.FeatureDistinct;
import com.dynacore.livemap.guidancesign.domain.GuidanceSignFeature;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.SynchronousSink;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

@Component
public class GuidanceSignFeatureDistinct implements FeatureDistinct<GuidanceSignFeature, GuidanceSignFeature> {

    @Autowired
    ModelMapper modelMapper;

    @Override
    public BiConsumer<? super GuidanceSignFeature, SynchronousSink<GuidanceSignFeature>> getFilter() {
        return (feature, sink) -> {
            sink.next(feature);
        };
    }
}
