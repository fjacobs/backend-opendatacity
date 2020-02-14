package com.dynacore.livemap.guidancesign.service;

import com.dynacore.livemap.core.service.DTODistinctInterface;
import com.dynacore.livemap.guidancesign.domain.GuidanceSignAggregate;
import com.dynacore.livemap.guidancesign.domain.GuidanceSignDTO;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import reactor.core.publisher.SynchronousSink;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

@Profile("GuidanceSign")
@Component
public class GuidanceSignDTODistinct implements DTODistinctInterface<GuidanceSignAggregate, GuidanceSignDTO> {

  Map<String, GuidanceSignDTO> dtoStore = new HashMap<>();

  @Override
  public BiConsumer<GuidanceSignAggregate, SynchronousSink<GuidanceSignDTO>> filter() {
    return (entity, sink) -> {
      GuidanceSignDTO dtoTest = new GuidanceSignDTO();
      dtoTest.setId("test");
      sink.next(dtoTest);
    };
  }
}
