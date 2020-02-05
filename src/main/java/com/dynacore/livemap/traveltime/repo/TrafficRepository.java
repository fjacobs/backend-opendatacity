package com.dynacore.livemap.traveltime.repo;

import com.dynacore.livemap.traveltime.service.filter.PubDateSizeResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.OffsetDateTime;

public interface TrafficRepository {
  Mono<Boolean> isNew(TravelTimeEntity entity);

  Mono<Integer> save(TravelTimeEntity entity);

  Mono<TravelTimeEntity> getLatest(TravelTimeEntity entity);

  Flux<TravelTimeEntity> getAllAscending();

  Flux<PubDateSizeResponse> getReplayMetaData();

  Flux<PubDateSizeResponse> getReplayMetaData(OffsetDateTime start, OffsetDateTime end);

  Flux<TravelTimeEntity> getFeatureDateRange(OffsetDateTime start, OffsetDateTime end);
}
