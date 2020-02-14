package com.dynacore.livemap.guidancesign.service;

import com.dynacore.livemap.core.service.FeatureDistinct;
import com.dynacore.livemap.guidancesign.domain.GuidanceSignFeatureImpl;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.SynchronousSink;

import java.util.function.BiConsumer;

@Component
public class GuidanceSignFeatureDistinct implements FeatureDistinct<GuidanceSignFeatureImpl, GuidanceSignFeatureImpl> {

    @Autowired
    ModelMapper modelMapper;

    @Override
    public BiConsumer<? super GuidanceSignFeatureImpl, SynchronousSink<GuidanceSignFeatureImpl>> getFilter() {
        return (feature, sink) -> {
            sink.next(feature);
        };
    }
}
