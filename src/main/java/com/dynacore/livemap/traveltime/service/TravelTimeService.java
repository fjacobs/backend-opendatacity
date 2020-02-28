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

import com.dynacore.livemap.core.Direction;
import com.dynacore.livemap.core.adapter.GeoJsonAdapter;
import com.dynacore.livemap.core.model.TrafficFeatureImpl;
import com.dynacore.livemap.core.model.TrafficMapDTO;
import com.dynacore.livemap.core.repository.EntityMapper;
import com.dynacore.livemap.core.repository.TrafficEntityImpl;
import com.dynacore.livemap.core.service.GeoJsonReactorService;
import com.dynacore.livemap.traveltime.domain.TravelTimeMapDTO;
import com.dynacore.livemap.traveltime.domain.TravelTimeFeatureImpl;
import com.dynacore.livemap.traveltime.repo.TravelTimeEntityImpl;
import com.dynacore.livemap.traveltime.repo.TravelTimeRepo;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Profile;
import org.springframework.data.r2dbc.core.DatabaseClient;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.function.Function;

import static org.springframework.data.r2dbc.query.Criteria.where;

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
public class TravelTimeService
    extends GeoJsonReactorService<TravelTimeEntityImpl, TravelTimeFeatureImpl, TravelTimeMapDTO> {

  private final Logger log = LoggerFactory.getLogger(TravelTimeService.class);

  public TravelTimeService(
      TravelTimeServiceConfig config,
      ObjectProvider<GeoJsonAdapter> geoJsonAdapterObjectProvider,
      TravelTimeImporter importer,
      TravelTimeRepo repo,
      TravelTimeDTODistinct entityDtoDistinct,
      TravelTimeFeatureDistinct featureDistinct)
      throws JsonProcessingException {
    super(config, geoJsonAdapterObjectProvider, importer, repo, entityDtoDistinct, featureDistinct);

    if (config.isSaveToDbEnabled()) {
      importedFeatures
              .map(TravelTimeEntityImpl::new)
              .map(repo::save)
              .subscribe(Mono::subscribe, error -> log.error("Error: " + error));
    }
  }

  public Flux<List<TravelTimeMapDTO>>  getPubDateReplay(
          OffsetDateTime startDate, Direction direction, @NonNull Duration interval) {
          return repo.getReplayDataTest(startDate, direction, interval)
                  .handle(dtoDistinctInterface.filter())
                  .bufferUntilChanged(TravelTimeMapDTO::getPubDate);
  }

//  public Flux<List<TravelTimeMapDTO>>  getPubDateReplay(
//          OffsetDateTime startDate, Direction direction, @NonNull Duration interval) {
//
//    log.info("Service replayv2. startdate: {}, Direction:  {}, Interval {}", startDate, direction, interval);
//
//
//    String sortDirection = direction.equals(Direction.BACKWARD) ? "desc" : "asc";
//    char startDirection = direction.equals(Direction.BACKWARD) ? '<' : '>';
//
//
//    String queryList;
//    if(startDate==null) {
//      queryList = "select pub_date from travel_time_entity group by pub_date";
//    } else {
//      queryList = String.format("select pub_date from travel_time_entity where pub_date %c '%s' group by pub_date order by pub_date %s", startDirection, startDate, sortDirection );
//    }
//
//    Mono<List<OffsetDateTime>> monoQueryList =  r2dbcClient
//            .execute(queryList)
//            .as(OffsetDateTime.class)
//            .fetch()
//            .all()
//            .collectList(); // Consume queryList from db, see https://gitter.im/R2DBC/r2dbc?at=5e4ee827c2c73b70a44a2bfe
//
//    return monoQueryList.flatMapIterable(Function.identity())
//            .delayElements(interval)
//            .flatMap(
//                    pub_date ->
//                            r2dbcClient
//                                    .select()
//                                    .from(TravelTimeEntityImpl.class)
//                                    .matching(where("pub_date").is(pub_date))
//                                    .fetch()
//                                    .all() )
//            .handle(dtoDistinctInterface.filter())
//            .bufferUntilChanged(TravelTimeMapDTO::getPubDate)
//            .delayElements(interval);
//  }

  public Flux<TravelTimeRepo.IdLoc> getLocationFlux(double yMin, double xMin, double yMax, double xMax) {
    return ((TravelTimeRepo) repo).getLocationsWithin(yMin, xMin,yMax,xMax);
  }
}
