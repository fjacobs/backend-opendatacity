package com.dynacore.livemap.core.service;

import com.dynacore.livemap.core.model.TrafficDTO;
import com.dynacore.livemap.core.repository.TrafficEntity;
import com.dynacore.livemap.core.repository.TrafficEntityInterface;
import reactor.core.publisher.SynchronousSink;

import java.util.function.BiConsumer;

public interface EntityDistinct<T extends TrafficEntityInterface, D extends TrafficDTO>  {

    BiConsumer<T, SynchronousSink<D>> filter( );
}
