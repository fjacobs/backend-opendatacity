package com.dynacore.livemap.core.service;

import com.dynacore.livemap.core.model.TrafficFeatureInterface;
import reactor.core.publisher.SynchronousSink;

import java.util.function.BiConsumer;

public interface FeatureDistinct<T extends TrafficFeatureInterface, R> {
    BiConsumer<? super T, SynchronousSink<R>> getFilter( );
}
