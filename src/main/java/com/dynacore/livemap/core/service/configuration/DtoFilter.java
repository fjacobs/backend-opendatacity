package com.dynacore.livemap.core.service.configuration;

import com.dynacore.livemap.core.model.TrafficDTO;
import com.dynacore.livemap.traveltime.domain.TravelTimeDTO;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.SynchronousSink;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

@Component
public class DtoFilter implements Filter<TrafficDTO,TrafficDTO> {

    @Autowired
    ModelMapper modelMapper;
    Map<String, TrafficDTO> dtoStore = new HashMap<>();

    @Override
    public BiConsumer<? super TrafficDTO, SynchronousSink<TrafficDTO>> filter() {
        return (newDTO, sink) -> {
            modelMapper.map(newDTO, TravelTimeDTO.class);

            TravelTimeDTO lastChangedDTO;
            if ((lastChangedDTO = (TravelTimeDTO) dtoStore.get(newDTO.getId())) != null) {
                if (!lastChangedDTO.equals(newDTO)) {
                    dtoStore.put(newDTO.getId(), newDTO);
                    if (!(lastChangedDTO.getVelocity().equals(((TravelTimeDTO) newDTO).getVelocity()))) {
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
