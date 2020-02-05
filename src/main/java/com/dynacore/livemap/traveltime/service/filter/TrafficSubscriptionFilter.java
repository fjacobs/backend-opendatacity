package com.dynacore.livemap.traveltime.service.filter;

import com.dynacore.livemap.traveltime.domain.TravelTimeDTO;
import reactor.core.publisher.SynchronousSink;

import java.util.function.BiConsumer;

@FunctionalInterface
public interface TrafficSubscriptionFilter {
    BiConsumer<? super TravelTimeDTO, SynchronousSink<Object>> passThroughFilter();
}
