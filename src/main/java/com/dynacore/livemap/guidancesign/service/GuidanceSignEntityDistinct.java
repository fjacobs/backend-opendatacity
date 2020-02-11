package com.dynacore.livemap.guidancesign.service;

import com.dynacore.livemap.core.service.EntityDistinct;
import com.dynacore.livemap.guidancesign.domain.GuidanceSignAggregate;
import com.dynacore.livemap.guidancesign.domain.GuidanceSignDTO;
import com.dynacore.livemap.guidancesign.domain.GuidanceSignEntity;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import reactor.core.publisher.SynchronousSink;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

@Profile("GuidanceSign")
@Component
public class GuidanceSignEntityDistinct
    implements EntityDistinct<GuidanceSignAggregate, GuidanceSignDTO> {

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
