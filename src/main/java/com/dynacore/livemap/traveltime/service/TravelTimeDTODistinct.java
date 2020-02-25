package com.dynacore.livemap.traveltime.service;

import com.dynacore.livemap.core.service.DTODistinctInterface;
import com.dynacore.livemap.traveltime.domain.TravelTimeMapDTO;
import com.dynacore.livemap.traveltime.repo.TravelTimeEntityImpl;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import reactor.core.publisher.SynchronousSink;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

@Profile("traveltime")
@Component
public class TravelTimeDTODistinct
    implements DTODistinctInterface<TravelTimeEntityImpl, TravelTimeMapDTO> {

  Map<String, TravelTimeMapDTO> dtoStore = new HashMap<>();

  @Override
  public BiConsumer<TravelTimeEntityImpl, SynchronousSink<TravelTimeMapDTO>> filter() {
    return (entity, sink) -> {
      TravelTimeMapDTO newDTO = new TravelTimeMapDTO(entity);
      TravelTimeMapDTO lastChangedDTO;
      if ((lastChangedDTO = dtoStore.get(newDTO.getId())) != null) {
        if (!lastChangedDTO.equals(newDTO)) {
          dtoStore.put(newDTO.getId(), newDTO);

          if ((lastChangedDTO.getVelocity() != null) && (newDTO.getVelocity() != null)) {
            if (!(lastChangedDTO.getVelocity().equals(newDTO.getVelocity()))) {
              sink.next(newDTO);
            }
          }
        }
      } else {
        dtoStore.put(newDTO.getId(), newDTO);
        sink.next(newDTO);
      }
    };
  }
}
