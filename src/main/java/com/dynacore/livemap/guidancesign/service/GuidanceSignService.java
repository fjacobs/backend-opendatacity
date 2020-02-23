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
package com.dynacore.livemap.guidancesign.service;

import com.dynacore.livemap.core.adapter.GeoJsonAdapter;
import com.dynacore.livemap.core.repository.EntityMapper;
import com.dynacore.livemap.core.service.GeoJsonReactorService;
import com.dynacore.livemap.guidancesign.domain.GuidanceSignAggregate;
import com.dynacore.livemap.guidancesign.domain.GuidanceSignEntity;
import com.dynacore.livemap.guidancesign.domain.GuidanceSignFeatureImpl;
import com.dynacore.livemap.guidancesign.repo.GuidanceSignRepo;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Profile;
import org.springframework.data.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Hooks;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;
// 4.3.2. The GEOMETRY_COLUMNS VIEW
//    GEOMETRY_COLUMNS is a view reading from database system catalogs.
/**
 * Road traffic traveltime service
 *
 * <p>This service will subscribe to a traffic information data source. It checks the GeoJson for
 * RFC compliance and calculates properties and publishes the new data in a reactive manner. The
 * data is automatically saved in a reactive database (R2DBC) and the last emitted signals are
 * cached for new subscribers.
 */
@Lazy(false)
@Profile("guidancesign")
@Service("guidanceSignService")
public class GuidanceSignService
    extends GeoJsonReactorService<GuidanceSignAggregate, GuidanceSignFeatureImpl, DisplayDTO> {

  Logger log = LoggerFactory.getLogger(GuidanceSignService.class);
  @Autowired DatabaseClient client;

  public GuidanceSignService(
      GuidanceSignProperties config,
      ObjectProvider<GeoJsonAdapter> adapter,
      GuidanceSignImporter importer,
      GuidanceSignRepo repo,
      DisplayDTODistinct entityDtoDistinct,
      GuidanceSignFeatureDistinct featureDistinct)
      throws JsonProcessingException {
    super(config, adapter, importer, repo, entityDtoDistinct, featureDistinct);
    Hooks.onOperatorDebug();
    Flux.from(importedFlux)
        //.map(GuidanceSignAggregate::new)
        .onBackpressureDrop(fc -> log.error("3. Drop on backpressure:"))
        .map(EntityMapper::geometryEntityConvertor)
        .map(repo::saveGeometry)
        .subscribe(Mono::subscribe, error -> log.error("Error: " + error));
  }

 // @Override
  public Flux<List<DisplayDTO>> replayHistoryGroup(Duration interval) {
    return repo.getAllAscending()
        .flatMap(
            aggregate -> {
              GuidanceSignEntity signEntity = aggregate.getGuidanceSignEntity();
              return aggregate
                  .getInnerDisplayEntities()
                  .map(
                      innerDisplayEntity ->
                          new DisplayDTO(
                              signEntity.getId(),
                              signEntity.getName(),
                              signEntity.getPubDate(),
                              signEntity.getRemoved(),
                              signEntity.getState(),
                              innerDisplayEntity.getDescription(),
                              innerDisplayEntity.getOutput(),
                              innerDisplayEntity.getOutputDescription()))
                  .windowUntilChanged(DisplayDTO::pubDate)
                  .flatMap(Flux::buffer)
                  .delayElements(interval)
                  .doOnNext(x -> log.info("Amount of distinct features: " + x.size()));
            });
  }
}
