package com.dynacore.livemap.guidancesign.service;

import com.dynacore.livemap.core.service.DTODistinctInterface;
import com.dynacore.livemap.guidancesign.domain.GuidanceSignAggregate;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import reactor.core.publisher.SynchronousSink;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;


// Returns a SynchronousSink for

@Profile("GuidanceSign")
@Component
public class DisplayDTODistinct implements DTODistinctInterface<GuidanceSignAggregate, DisplayDTO>
{

  Map<String, DisplayDTO> dtoStore = new HashMap<>();

  @Override
  public BiConsumer<GuidanceSignAggregate, SynchronousSink<DisplayDTO>>  filter()
  {

    return (aggregate, sink) -> {
      //      GuidanceSignEntity signEntity = aggregate.getGuidanceSignEntity();
      //
      //        return Mono.just(aggregate).flatMap(agg -> {
      //        agg.getInnerDisplayEntities().map( innerDisplayEntity -> {
      //            var display =
      //                    new DisplayDTO(
      //                            signEntity.getId(),
      //                            signEntity.getName(),
      //                            signEntity.getPubDate(),
      //                            signEntity.getRemoved(),
      //                            signEntity.getState(),
      //                            innerDisplayEntity.getDescription(),
      //                            innerDisplayEntity.getOutput(),
      //                            innerDisplayEntity.getOutputDescription());
      //            sink.next(display);
      //            return display;
      //        }); }
    };
  }


}



