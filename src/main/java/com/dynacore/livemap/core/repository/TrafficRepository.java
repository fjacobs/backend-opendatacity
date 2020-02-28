package com.dynacore.livemap.core.repository;

import com.dynacore.livemap.core.Direction;
import com.dynacore.livemap.core.PubDateSizeResponse;
import com.dynacore.livemap.core.model.TrafficDTO;
import com.dynacore.livemap.traveltime.domain.TravelTimeMapDTO;
import com.dynacore.livemap.traveltime.repo.TravelTimeEntityImpl;
import org.springframework.data.r2dbc.core.DatabaseClient;
import org.springframework.lang.NonNull;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;

import static org.springframework.data.r2dbc.query.Criteria.where;

public interface TrafficRepository<T extends TrafficEntity> {


  Mono<Boolean> isNew(T entity);

  Mono<Void> save(T entity);

  Mono<T> getLatest(String entityId);

  Flux<T> getAllAscending();


  Flux<PubDateSizeResponse> getReplayMetaData();

  Flux<PubDateSizeResponse> getReplayMetaData(OffsetDateTime start, OffsetDateTime end);

  Flux<T> getFeatureDateRange(OffsetDateTime start, OffsetDateTime end);

  Mono<Void> saveGeometry(GeometryEntity locationEntityConvertor);

   Flux<T> getReplayData(OffsetDateTime start, Direction streamDirection);
   Flux<T> getReplayDataTest(OffsetDateTime start, Direction streamDirection, Duration interval);

}
