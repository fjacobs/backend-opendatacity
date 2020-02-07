package com.dynacore.livemap.core.service.configuration;

import com.dynacore.livemap.core.model.TrafficDTO;
import com.dynacore.livemap.core.repository.TrafficEntity;
import reactor.core.publisher.SynchronousSink;

import java.util.function.BiConsumer;

public interface RepoFilter {

    BiConsumer<TrafficEntity, SynchronousSink<TrafficDTO>> filter( );

}
