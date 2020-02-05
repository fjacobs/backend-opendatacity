/*
 * Copyright 2002-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.dynacore.livemap.traveltime.service;

import com.dynacore.livemap.core.geojson.GeoJsonObjectVisitorWrapper;
import com.dynacore.livemap.core.geojson.TrafficFeature;
import com.dynacore.livemap.core.model.TrafficDTO;
import com.dynacore.livemap.core.service.GeoJsonAdapter;
import com.dynacore.livemap.traveltime.domain.TravelTimeDTO;
import com.dynacore.livemap.traveltime.repo.TravelTimeRepo;
import com.dynacore.livemap.traveltime.service.visitor.CalculateTravelTime;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.geojson.Feature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import reactor.core.publisher.SynchronousSink;

import java.util.function.BiConsumer;

/**
 * Road traffic traveltime service
 *
 * <p>This service will subscribe to a traffic information data source. It checks the GeoJson for
 * RFC compliance and calculates properties and publishes the new data in a reactive manner. The
 * data is automatically saved in a reactive database (R2DBC) and the last emitted signals are
 * cached for new subscribers.
 */
@Lazy(false)
@Profile("traveltime")
@Service("travelTimeService")
public class TravelTimeReactorService extends GeoJsonReactorService {

  public TravelTimeReactorService(
          TravelTimeRepo repo, GeoJsonAdapter retriever, TravelTimeServiceConfig config)
      throws JsonProcessingException {
    super(repo, retriever, config);
  }

  @Override
  protected GeoJsonObjectVisitorWrapper<Feature> processFeature() {
    return new CalculateTravelTime();
  }

  @Override
  protected BiConsumer<? super TrafficFeature, SynchronousSink<TrafficFeature>> liveUpdateFilter() {
    return (feature, sink) -> {
      Integer newHash = DistinctUtil.hashCodeNoRetDate.apply(feature);
      Integer oldHash;

      if ((oldHash = geoJsonStore.get(feature.getId())) != null) {
        if (!newHash.equals(oldHash)) {
          geoJsonStore.put(feature.getId(), newHash);

          sink.next(feature);
        }
      } else {
        geoJsonStore.put(feature.getId(), newHash);
        sink.next(feature);
      }
    };
  }

  @Override
  protected BiConsumer<? super TrafficDTO, SynchronousSink<TrafficDTO>> replayDtoFilter() {
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
