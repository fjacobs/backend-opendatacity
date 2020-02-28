package com.dynacore.livemap.guidancesign.repo;

import com.dynacore.livemap.core.Direction;
import com.dynacore.livemap.core.PubDateSizeResponse;
import com.dynacore.livemap.core.repository.GeometryEntity;
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

import java.time.Duration;
import java.time.OffsetDateTime;

import static org.springframework.data.r2dbc.query.Criteria.where;

@Profile("guidancesign")
@Repository("guidanceSignRepository")
public class GuidanceSignRepo implements TrafficRepository<GuidanceSignAggregate> {

  private final DatabaseClient databaseClient;
  private static final Logger log = LoggerFactory.getLogger(GuidanceSignRepo.class);

  public GuidanceSignRepo(DatabaseClient databaseClient) {
    this.databaseClient = databaseClient;
  }

  @Override
  public Mono<Boolean> isNew(GuidanceSignAggregate aggregate) {
    var entity = aggregate.getGuidanceSignEntity();
    return databaseClient
        .select()
        .from(GuidanceSignEntity.class)
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

  public Mono<Void> saveGeometry(GeometryEntity geometryEntity) {
    return databaseClient
            .execute("INSERT into guidancesign_geometries(id, geo_type, data_type, geom) VALUES( '" + geometryEntity.id() + "', '" + geometryEntity.geo_type() + "', '" + geometryEntity.data_type() + "', ST_GeomFromText('" + geometryEntity.geom().toString() + "',4326));")
            .fetch()
            .rowsUpdated()
            .then();
  }

    public Flux<GuidanceSignAggregate> getReplayData(OffsetDateTime start, Direction streamDirection) {
        return null;
    }

    @Override
    public Flux<GuidanceSignAggregate> getReplayDataTest(OffsetDateTime start, Direction streamDirection, Duration interval) {
        return null;
    }

    public Mono<Void> save(GuidanceSignAggregate aggregate) {

    return Mono.just(aggregate).flatMap(
            aggregate1 ->      databaseClient
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
            .then()).onErrorResume(x-> {
                log.warn("Error writing to db: " + x.getMessage());
                return Mono.empty();
            });
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
