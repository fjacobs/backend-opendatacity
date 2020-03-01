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
import com.dynacore.livemap.core.model.TrafficDTO;
import com.dynacore.livemap.core.service.GeoJsonReactorService;
import com.dynacore.livemap.traveltime.domain.FeatureLocation;
import com.dynacore.livemap.traveltime.domain.TravelTimeMapDTO;
import com.dynacore.livemap.traveltime.domain.TravelTimeFeatureImpl;
import com.dynacore.livemap.traveltime.repo.TravelTimeEntityImpl;
import com.dynacore.livemap.traveltime.repo.TravelTimeRepo;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Profile;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.Comparator;
import java.util.concurrent.atomic.AtomicLong;

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

  private static final AtomicLong atomicDuration = new AtomicLong(1000L);
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

  // Flux will 'restart' on backward/forward, not when changing interval
  public Flux<TravelTimeMapDTO> getPubDateReplay(
      OffsetDateTime startDate, Direction direction, @NonNull Integer interval) {

    Flux<Tuple2<OffsetDateTime, Flux<TravelTimeEntityImpl>>> deferredSqlStatements;
    atomicDuration.set(interval);

    if (startDate == null) {
      deferredSqlStatements = repo.getReplayQueries();
    } else if (direction == Direction.FORWARD) {
      deferredSqlStatements =
          repo.getReplayQueries()
              .sort(Comparator.comparing(Tuple2::getT1))
              .filter(sqlTuple -> sqlTuple.getT1().isAfter(startDate));
    } else {
      deferredSqlStatements =
          repo.getReplayQueries()
              .sort(Comparator.comparing(Tuple2::getT1, Comparator.reverseOrder()))
              .filter(sqlTuple -> sqlTuple.getT1().isBefore(startDate));
    }

    return deferredSqlStatements
        .concatMap(v -> {
          log.info("delayelements; {}", atomicDuration.get());
          return Mono.just(v).delayElement(Duration.ofMillis(atomicDuration.get()));
        })
        .flatMap(Tuple2::getT2)
        .handle(dtoDistinctInterface.filter())
        .bufferUntilChanged(TrafficDTO::pubDate)
        .flatMapIterable(a->a);
  }

  public Flux<FeatureLocation> getLocationFlux(
      double yMin, double xMin, double yMax, double xMax) {
    return ((TravelTimeRepo) repo).getLocationsWithin(yMin, xMin, yMax, xMax);
  }

  public void changeReplayInterval(Integer interval) {
    log.info("changeReplayInterval to: {}", interval);
    atomicDuration.set(interval);
  }
}
