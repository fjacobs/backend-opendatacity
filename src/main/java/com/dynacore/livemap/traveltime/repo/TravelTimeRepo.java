package com.dynacore.livemap.traveltime.repo;

import com.dynacore.livemap.core.Direction;
import com.dynacore.livemap.core.PubDateSizeResponse;
import com.dynacore.livemap.core.model.TrafficDTO;
import com.dynacore.livemap.core.repository.GeometryEntity;
import com.dynacore.livemap.core.repository.TrafficRepository;
import com.dynacore.livemap.traveltime.domain.TravelTimeMapDTO;
import org.locationtech.jts.geom.Geometry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.data.r2dbc.core.DatabaseClient;
import org.springframework.data.r2dbc.query.Criteria;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;
import reactor.bool.BooleanUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.function.Function;

import static org.springframework.data.domain.Sort.Order.asc;
import static org.springframework.data.domain.Sort.Order.desc;
import static org.springframework.data.r2dbc.query.Criteria.where;

@Profile("traveltime")
@Repository("travelTimeRepository")
public class TravelTimeRepo implements TrafficRepository<TravelTimeEntityImpl> {

  private static final Logger logger = LoggerFactory.getLogger(TravelTimeRepo.class);
  private final DatabaseClient r2dbcClient;

  public TravelTimeRepo(DatabaseClient databaseClient) {
    this.r2dbcClient = databaseClient;
  }

  @Override
  public Mono<Boolean> isNew(TravelTimeEntityImpl entity) {

    return r2dbcClient
        .select()
        .from(TravelTimeEntityImpl.class)
        .matching(where("id").is(entity.getId()).and("pubDate").is(entity.getPubDate()))
        .fetch()
        .first()
        .hasElement()
        .as(BooleanUtils::not);
  }

  @Override
  public Mono<Void> save(TravelTimeEntityImpl entity) {

    return Mono.just(entity)
        .map(
            newEntity ->
                r2dbcClient
                    .insert()
                    .into(TravelTimeEntityImpl.class)
                    .using(newEntity)
                    .fetch()
                    .rowsUpdated()
                    .then())
        .then();
  }

  public Mono<Void> saveGeometry(GeometryEntity geometryEntity) {
    return r2dbcClient
        .execute(
            "UPDATE traveltime_geometries(id, geo_type, data_type, geom) VALUES( '"
                + geometryEntity.id()
                + "', '"
                + geometryEntity.geo_type()
                + "', '"
                + geometryEntity.data_type()
                + "', ST_GeomFromText('"
                + geometryEntity.geom().toString()
                + "',4326));")
        .fetch()
        .rowsUpdated()
        .then();
  }

  public Flux<IdLoc> getLocationsWithin(double yMin, double xMin, double yMax, double xMax) {

    return r2dbcClient
        .execute(
            "SELECT id, geom FROM traveltime_geometries where geom && ST_MakeEnvelope("
                + xMin
                + ", "
                + yMin
                + ","
                + xMax
                + ","
                + yMax
                + ", 4326)")
        .map((row, rowMetadata) -> new IdLoc((String) row.get(0), (Geometry) row.get(1)))
        .all();
  };

  @Override
  public Mono<TravelTimeEntityImpl> getLatest(String entityId) {
    return r2dbcClient
        .execute(
            "     SELECT id, name, pub_date, our_retrieval, type, length, travel_time, velocity \n"
                + "     FROM public.travel_time_entity\n"
                + "\t   WHERE pub_date=(\n"
                + "                SELECT MAX(pub_date) FROM public.travel_time_entity WHERE id='"
                + entityId
                + "');")
        .as(TravelTimeEntityImpl.class)
        .fetch()
        .first();
  }

  public Mono<OffsetDateTime> getOldestDate(String entityId) {
    return r2dbcClient
        .execute(
            "     SELECT  pub_date\n"
                + "     FROM public.travel_time_entity\n"
                + "\t   WHERE pub_date=(\n"
                + "                SELECT MAX(pub_date) FROM public.travel_time_entity WHERE id='"
                + entityId
                + "');")
        .as(OffsetDateTime.class)
        .fetch()
        .first();
  }

  @Override
  public Flux<TravelTimeEntityImpl> getAllAscending() {

    return r2dbcClient
        .execute("select distinct pub_date from travel_time_entity group by pub_date")
        .as(OffsetDateTime.class)
        .fetch()
        .all()
        .collectList() // see https://gitter.im/R2DBC/r2dbc?at=5e4ee827c2c73b70a44a2bfe
        .flatMapIterable(Function.identity()) // Consume query from db
        .transformDeferred(
            pub_date ->
                r2dbcClient
                    .select()
                    .from(TravelTimeEntityImpl.class)
                    .matching(where("pub_date").is(pub_date))
                    .fetch()
                    .all());
  }

  public Flux<TravelTimeEntityImpl> getReplayData(OffsetDateTime startDate, Direction direction) {
    return null;
  }

  // public Flux<Flux<TravelTimeEntityImpl>> getReplayDataTest(OffsetDateTime startDate, Direction
  // direction, Duration interval) {
  public Flux<TravelTimeEntityImpl> getReplayDataTest(
      OffsetDateTime startDate, Direction direction, Duration interval) {

    String sortDirection = direction.equals(Direction.BACKWARD) ? "desc" : "asc";
    char startDirection = direction.equals(Direction.BACKWARD) ? '<' : '>';

    String queryList;
    if (startDate == null) {
      queryList = "select pub_date from travel_time_entity group by pub_date";
    } else {
      queryList =
          String.format(
              "select pub_date from travel_time_entity where pub_date %c '%s' group by pub_date order by pub_date %s",
              startDirection, startDate, sortDirection);
    }

    Mono<List<OffsetDateTime>> monoQueryList =
        r2dbcClient
            .execute(queryList)
            .as(OffsetDateTime.class)
            .fetch()
            .all()
            .collectList(); // Consume queryList from db, see
    // https://gitter.im/R2DBC/r2dbc?at=5e4ee827c2c73b70a44a2bfe

    return monoQueryList
        .flatMapIterable(Function.identity()) // Transform strings into publishers
        .delayElements(interval)
        .concatMap(
            pub_date ->
                r2dbcClient
                    .select()
                    .from(TravelTimeEntityImpl.class)
                    .matching(where("pub_date").is(pub_date))
                    .fetch()
                    .all());
  }

  @Override
  public Flux<PubDateSizeResponse> getReplayMetaData() {
    return r2dbcClient
        .execute(
            " SELECT pub_date, COUNT (pub_date) FROM public.travel_time_entity GROUP BY travel_time_entity.pub_date ORDER BY pub_date ASC;")
        .as(PubDateSizeResponse.class)
        .fetch()
        .all();
  }

  @Override
  public Flux<PubDateSizeResponse> getReplayMetaData(OffsetDateTime start, OffsetDateTime end) {
    return r2dbcClient
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
  public Flux<TravelTimeEntityImpl> getFeatureDateRange(OffsetDateTime start, OffsetDateTime end) {
    return r2dbcClient
        .execute(
            "    SELECT pub_date, COUNT (pub_date)\n"
                + "    FROM\n"
                + "             public.travel_time_entity\n"
                + "    WHERE\n"
                + "    pub_date >= '"
                + start
                + "'\n"
                + "\n"
                + "    GROUP BY travel_time_entity.pub_date ORDER BY pub_date DESC;")
        .as(TravelTimeEntityImpl.class)
        .fetch()
        .all();
  }

  public record IdLoc(String id, Geometry geom) {}
}
