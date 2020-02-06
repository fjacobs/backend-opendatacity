package com.dynacore.livemap.core.service.configuration;

import reactor.core.publisher.SynchronousSink;

import java.util.function.BiConsumer;

public interface Filter<T, R> {
    BiConsumer<? super T, SynchronousSink<R>> filter( );
}
