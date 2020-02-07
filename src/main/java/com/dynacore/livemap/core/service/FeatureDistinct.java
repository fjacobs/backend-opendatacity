package com.dynacore.livemap.core.service;

import com.dynacore.livemap.core.model.TrafficFeature;
import reactor.core.publisher.SynchronousSink;

import java.util.function.BiConsumer;

public interface FeatureDistinct<T extends TrafficFeature, R> {
    BiConsumer<? super T, SynchronousSink<R>> getFilter( );
}
