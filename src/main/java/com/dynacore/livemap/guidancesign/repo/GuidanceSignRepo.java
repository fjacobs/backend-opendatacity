package com.dynacore.livemap.guidancesign.repo;

import com.dynacore.livemap.core.PubDateSizeResponse;
import com.dynacore.livemap.core.repository.TrafficRepository;
import com.dynacore.livemap.guidancesign.domain.GuidanceSignAggregate;
import com.dynacore.livemap.guidancesign.domain.GuidanceSignEntity;
import com.dynacore.livemap.guidancesign.domain.InnerDisplayEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.data.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import reactor.bool.BooleanUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.OffsetDateTime;

import static org.springframework.data.r2dbc.query.Criteria.where;

@Profile("guidancesign")
@Repository("guidanceSignRepository")
public class GuidanceSignRepo implements TrafficRepository<GuidanceSignAggregate> {

  private DatabaseClient databaseClient;
  private static final Logger log = LoggerFactory.getLogger(GuidanceSignRepo.class);

  public GuidanceSignRepo(DatabaseClient databaseClient) {
    this.databaseClient = databaseClient;
  }

  @Override
  public Mono<Boolean> isNew(GuidanceSignAggregate aggregate) {
    var entity = aggregate.getGuidanceSignEntity();
    return databaseClient
        .select()
        .from(GuidanceSignAggregate.class)
        .matching(where("id").is(entity.getId()).and("pubDate").is(entity.getPubDate()))
        .fetch()
        .first()
        .hasElement()
        .as(BooleanUtils::not);
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
  public Flux<GuidanceSignAggregate> getFeatureDateRange(OffsetDateTime start, OffsetDateTime end) {
    return null;
  }

  @Override
  public Mono<Void> save(GuidanceSignAggregate aggregate) {

    return databaseClient
        .insert()
        .into(GuidanceSignEntity.class)
        .using(aggregate.getGuidanceSignEntity())
        .map(
            (row, rowMetadata) -> {
              aggregate.setFk(row.get("pkey", Integer.class));
              return row;
            })
        .one()
        .then(
            databaseClient
                .insert()
                .into(InnerDisplayEntity.class)
                .using(aggregate.getInnerDisplayEntities())
                .fetch()
                .all()
                .then())
        .then();
  }

  @Override
  public Mono<GuidanceSignAggregate> getLatest(String entityId) {
    return null;
  }

  @Override
  public Flux<GuidanceSignAggregate> getAllAscending() {
    return databaseClient
        .select()
        .from("guidance_sign_entity")
        .as(GuidanceSignEntity.class)
        .fetch()
        .all()
        .map(
            guidanceSignEntity -> {
              GuidanceSignAggregate aggregateFlux = new GuidanceSignAggregate(guidanceSignEntity);
              Flux<InnerDisplayEntity> inner =
                  databaseClient
                      .select()
                      .from("inner_display_entity")
                      .matching(where("id").is(guidanceSignEntity.getId()))
                      .as(InnerDisplayEntity.class)
                      .fetch()
                      .all()
                      .doOnNext(innerDisplay -> System.out.println(innerDisplay.getId()));

              aggregateFlux.setInnerDisplayEntities(inner);
              return aggregateFlux;
            });
  }
}
