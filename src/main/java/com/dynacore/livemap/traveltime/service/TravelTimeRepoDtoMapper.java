package com.dynacore.livemap.traveltime.service;

import com.dynacore.livemap.core.model.TrafficDTO;
import com.dynacore.livemap.core.repository.TrafficEntity;
import com.dynacore.livemap.core.service.configuration.RepoFilter;
import com.dynacore.livemap.traveltime.domain.TravelTimeDTO;
import com.dynacore.livemap.traveltime.repo.TravelTimeEntity;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.SynchronousSink;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

@Component
public class TravelTimeRepoDtoMapper implements RepoFilter {

  @Autowired ModelMapper modelMapper;
  Map<String, TravelTimeDTO> dtoStore = new HashMap<>();

  @Override
  public BiConsumer<TrafficEntity, SynchronousSink<TrafficDTO>> filter() {
    return (entity, sink) -> {
      TravelTimeEntity entity1 = (TravelTimeEntity) entity;
      TravelTimeDTO newDTO = new TravelTimeDTO(entity1);

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
