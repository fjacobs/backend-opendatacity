package com.dynacore.livemap.parking.repo;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.OffsetDateTime;

public interface ParkingRepository {
  Mono<Boolean> isNew(ParkingEntity entity);

  Mono<Integer> save(ParkingEntity entity);

  Mono<ParkingEntity> getLatest(ParkingEntity entity);

  Flux<ParkingEntity> getAllAscending();

  Flux<ParkingEntity> getFeatureDateRange(OffsetDateTime start, OffsetDateTime end);
}
