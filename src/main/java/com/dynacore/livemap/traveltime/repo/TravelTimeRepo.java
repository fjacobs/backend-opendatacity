package com.dynacore.livemap.traveltime.repo;

import com.dynacore.livemap.traveltime.service.filter.PubDateSizeResponse;
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

@Profile("traveltime")
@Repository("travelTimeRepository")
public class TravelTimeRepo implements TravelTimeRepository {

  private DatabaseClient databaseClient;
  private static final Logger logger = LoggerFactory.getLogger(TravelTimeRepo.class);

  public TravelTimeRepo(DatabaseClient databaseClient) {
    this.databaseClient = databaseClient;
  }

  @Override
  public Mono<Boolean> isNew(TravelTimeEntity entity) {
    return databaseClient
        .select()
        .from(TravelTimeEntity.class)
        .matching(where("id").is(entity.getId()).and("pubDate").is(entity.getPubDate()))
        .fetch()
        .first()
        .hasElement()
        .as(BooleanUtils::not);
  }

  @Override
  public Mono<Integer> save(TravelTimeEntity entity) {
    return Mono.just(entity)
        .filterWhen(this::isNew)
        .flatMap(
            newEntity ->
                databaseClient
                    .insert()
                    .into(TravelTimeEntity.class)
                    .using(newEntity)
                    .fetch()
                    .rowsUpdated());
  }

  @Override
  public Mono<TravelTimeEntity> getLatest(TravelTimeEntity entity) {
    return databaseClient
        .execute(
            "     SELECT id, name, pub_date, retrieved_from_third_party, type, length, travel_time, velocity \n"
                + "     FROM public.travel_time_entity\n"
                + "\t   WHERE pub_date=(\n"
                + "                SELECT MAX(pub_date) FROM public.travel_time_entity WHERE id='"
                + entity.getId()
                + "');")
        .as(TravelTimeEntity.class)
        .fetch()
        .first();
  }

  @Override
  public Flux<TravelTimeEntity> getAllAscending() {
    return databaseClient
        .execute("SELECT * FROM public.travel_time_entity ORDER BY pub_date ASC")
        .as(TravelTimeEntity.class)
        .fetch()
        .all();
  }

  @Override
  public Flux<PubDateSizeResponse> getReplayMetaData() {
    return databaseClient
        .execute(
            " SELECT pub_date, COUNT (pub_date) FROM public.travel_time_entity GROUP BY travel_time_entity.pub_date ORDER BY pub_date ASC;")
        .as(PubDateSizeResponse.class)
        .fetch()
        .all();
  }

  @Override
  public Flux<PubDateSizeResponse> getReplayMetaData(OffsetDateTime start, OffsetDateTime end) {
    return databaseClient
        .execute(
            "    SELECT pub_date, COUNT (pub_date)\n"
                + "    FROM\n"
                + "             public.travel_time_entity\n"
                + "    WHERE\n"
                + "    pub_date >= '"
                + start
                + "'\n"
                + "    AND    pub_date <='"
                + end
                + "'\n"
                + "\n"
                + "    GROUP BY travel_time_entity.pub_date ORDER BY pub_date ASC;")
        .as(PubDateSizeResponse.class)
        .fetch()
        .all();
  }

  @Override
  public Flux<TravelTimeEntity> getFeatureDateRange(OffsetDateTime start, OffsetDateTime end) {
    return databaseClient
        .execute(
            "    SELECT pub_date, COUNT (pub_date)\n"
                + "    FROM\n"
                + "             public.travel_time_entity\n"
                + "    WHERE\n"
                + "    pub_date >= '"
                + start
                + "'\n"
                + "    AND    pub_date <= '"
                + end
                + "'\n"
                + "\n"
                + "    GROUP BY travel_time_entity.pub_date ORDER BY pub_date DESC;")
        .as(TravelTimeEntity.class)
        .fetch()
        .all();
  }
}
