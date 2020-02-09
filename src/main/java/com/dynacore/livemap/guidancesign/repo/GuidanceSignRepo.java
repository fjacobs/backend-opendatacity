package com.dynacore.livemap.guidancesign.repo;

import com.dynacore.livemap.core.PubDateSizeResponse;
import com.dynacore.livemap.core.repository.TrafficRepository;
import com.dynacore.livemap.guidancesign.domain.GuidanceSignEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.data.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.OffsetDateTime;

@Profile("guidancesign")
@Repository("guidanceSignRepository")
public class GuidanceSignRepo implements TrafficRepository<GuidanceSignEntity> {

  private DatabaseClient databaseClient;
  private static final Logger logger = LoggerFactory.getLogger(GuidanceSignRepo.class);

  public GuidanceSignRepo(DatabaseClient databaseClient) {
    this.databaseClient = databaseClient;
  }

  @Override
  public Mono<Boolean> isNew(GuidanceSignEntity entity) {
    return null;
  }

  @Override
  public Mono<Integer> save(GuidanceSignEntity entity) {
    return null;
  }

  @Override
  public Mono<GuidanceSignEntity> getLatest(String entityId) {
    return null;
  }

  @Override
  public Flux<GuidanceSignEntity> getAllAscending() {
    return null;
  }

  @Override
  public Flux<PubDateSizeResponse> getReplayMetaData() {
    return null;
  }

  @Override
  public Flux<PubDateSizeResponse> getReplayMetaData(OffsetDateTime start, OffsetDateTime end) {
    return null;
  }

  @Override
  public Flux<GuidanceSignEntity> getFeatureDateRange(OffsetDateTime start, OffsetDateTime end) {
    return null;
  }
}
