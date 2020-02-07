package com.dynacore.livemap.traveltime.service;

import com.dynacore.livemap.core.service.EntityDistinct;
import com.dynacore.livemap.traveltime.domain.TravelTimeDTO;
import com.dynacore.livemap.traveltime.repo.TravelTimeEntity;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import reactor.core.publisher.SynchronousSink;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

@Profile("traveltime")
@Component
public class TravelTimeEntityDistinct implements EntityDistinct<TravelTimeEntity, TravelTimeDTO> {


  Map<String, TravelTimeDTO> dtoStore = new HashMap<>();

  @Override
  public BiConsumer<TravelTimeEntity, SynchronousSink<TravelTimeDTO>> filter() {
    return (entity, sink) -> {

      TravelTimeDTO newDTO = new TravelTimeDTO(entity);
      TravelTimeDTO lastChangedDTO;
      if ((lastChangedDTO = dtoStore.get(newDTO.getId())) != null) {
        if (!lastChangedDTO.equals(newDTO)) {
          dtoStore.put(newDTO.getId(), newDTO);
          if (!(lastChangedDTO.getVelocity().equals(newDTO.getVelocity()))) {
            sink.next(newDTO);
          }
        }
      } else {
        dtoStore.put(newDTO.getId(), newDTO);
        sink.next(newDTO);
      }
    };
  }
}
