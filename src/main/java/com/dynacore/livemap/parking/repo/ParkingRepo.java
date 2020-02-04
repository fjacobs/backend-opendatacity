package com.dynacore.livemap.parking.repo;

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

@Profile("parking")
@Repository("parkingRepository")
public class ParkingRepo implements ParkingRepository {

    private DatabaseClient databaseClient;
    private final static Logger logger = LoggerFactory.getLogger(ParkingRepo.class);

    public ParkingRepo(DatabaseClient databaseClient) {
        this.databaseClient = databaseClient;
    }

    @Override
    public Mono<Boolean> isNew(ParkingEntity entity) {
        return databaseClient.select().from(ParkingEntity.class)
                .matching(where("id")
                        .is(entity.getId())
                        .and("pubDate")
                        .is(entity.getPubDate()))
                .fetch()
                .first()
                .hasElement()
                .as(BooleanUtils::not);
    }

    @Override
    public Mono<Integer> save(ParkingEntity entity) {
        return Mono.just(entity)
                .filterWhen(this::isNew)
                .flatMap(newEntity -> databaseClient.insert()
                        .into(ParkingEntity.class)
                        .using(newEntity)
                        .fetch()
                        .rowsUpdated());
    }

    @Override
    public Mono<ParkingEntity> getLatest(ParkingEntity entity) {
        return databaseClient.execute(
                "     SELECT id, name, pub_date, retrieved_from_third_party, type, length, travel_time, velocity \n" +
                        "     FROM public.travel_time_entity\n" +
                        "\t   WHERE pub_date=(\n" +
                        "                SELECT MAX(pub_date) FROM public.travel_time_entity WHERE id='" + entity.getId() + "');")
                .as(ParkingEntity.class)
                .fetch()
                .first();
    }

    @Override
    public Flux<ParkingEntity> getAllAscending() {
        return databaseClient.execute("SELECT * FROM public.travel_time_entity ORDER BY pub_date ASC")
                .as(ParkingEntity.class)
                .fetch()
                .all();
    }


    @Override
    public Flux<ParkingEntity> getFeatureDateRange(OffsetDateTime start, OffsetDateTime end) {
        return databaseClient.execute("    SELECT pub_date, COUNT (pub_date)\n" +
                "    FROM\n" +
                "             public.travel_time_entity\n" +
                "    WHERE\n" +
                "    pub_date >= '" + start + "'\n" +
                "    AND    pub_date <= '" + end + "'\n" +
                "\n" +
                "    GROUP BY travel_time_entity.pub_date ORDER BY pub_date DESC;")
                .as(ParkingEntity.class)
                .fetch()
                .all();
    }

}


