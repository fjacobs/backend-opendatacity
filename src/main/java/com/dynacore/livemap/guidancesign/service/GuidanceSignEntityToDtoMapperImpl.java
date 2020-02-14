package com.dynacore.livemap.guidancesign.service;

import com.dynacore.livemap.core.EntityToDtoMapper;
import com.dynacore.livemap.core.model.TrafficDTO;
import com.dynacore.livemap.guidancesign.domain.GuidanceSignAggregate;

public class GuidanceSignEntityToDtoMapperImpl
    implements EntityToDtoMapper<GuidanceSignAggregate, TrafficDTO> {

    @Override
    public GuidanceSignAggregate map(TrafficDTO entity) {
        return null;
    }
}
