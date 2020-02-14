package com.dynacore.livemap.core.service;

import com.dynacore.livemap.core.model.TrafficDTO;
import com.dynacore.livemap.core.repository.TrafficEntity;
import reactor.core.publisher.SynchronousSink;

import java.util.function.BiConsumer;

public interface DTODistinctInterface<T extends TrafficEntity, D extends TrafficDTO>  {

    BiConsumer<T , SynchronousSink<D>> filter( );
}
