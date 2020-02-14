package com.dynacore.livemap.core;

import com.dynacore.livemap.core.model.TrafficDTO;
import com.dynacore.livemap.core.repository.TrafficEntity;


public interface EntityToDtoMapper<T extends TrafficEntity, R extends TrafficDTO> {

  T map(R entity);
}
