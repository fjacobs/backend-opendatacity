package com.dynacore.livemap.traveltime;

import io.r2dbc.client.R2dbc;
import io.r2dbc.pool.ConnectionPool;
import io.r2dbc.pool.ConnectionPoolConfiguration;
import io.r2dbc.spi.Connection;
import io.r2dbc.spi.ConnectionFactories;
import io.r2dbc.spi.ConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Profile;
import org.springframework.data.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;

import static io.r2dbc.postgresql.PostgresqlConnectionFactoryProvider.POSTGRESQL_DRIVER;

@Profile("traveltime")
@Repository("roadAsyncRepo")
public class RoadAsyncRepo {
    private ConnectionFactory myFactory;
    private Logger log = LoggerFactory.getLogger(RoadAsyncRepo.class);
    private DatabaseClient client;
    private ConnectionPool pool;
    Mono<Connection> connectionMono;
    R2dbc r2;

    @Autowired
    public RoadAsyncRepo(@Qualifier("myFactory") ConnectionFactory myFactory) {

        this.myFactory = myFactory;
        ConnectionPoolConfiguration configuration = ConnectionPoolConfiguration.builder(myFactory)
                .maxIdleTime(Duration.ofMillis(1000))
                .build();
        pool = new ConnectionPool(configuration);
        connectionMono  = pool.create();
        client = DatabaseClient.create(pool);


        r2 = new R2dbc(ConnectionFactories.get(
                String.format("r2dbc:pool:%s://%s:%s@%s:%d/%s", POSTGRESQL_DRIVER, "postgres", "admin", "localhost", 5432, "trafficdata")));

    //    r2 = new R2dbc(pool);


    }




//    Connection connection = …;
//    Mono<Void> release = connection.close();

    public void closeConnection() {
        pool.close();
    }


    public Flux<Integer> testSelectX() {
         return r2.inTransaction(handle ->
                handle.execute("INSERT INTO test VALUES ($1)", 100))
                .thenMany(r2.inTransaction(handle ->
                        handle.select("SELECT value FROM test")
                                .mapResult(result -> result.map((row, rowMetadata) -> row.get("value", Integer.class)))));

    }



    public Mono<TravelTimeEntity> testSelect() {
        return
                client.execute("select * from travel_time_entity where Column id = 'RWS01_MONIBAS_0051hrl0092rb0'")
                .as(TravelTimeEntity.class)
                .fetch()
                .first();
    }


    public void save(TravelTimeEntity travelTimeEntity) {
        try {
//             client.insert()
//                    .into(TravelTimeEntity.class)
//                    .using(travelTimeEntity)
//                    .then()
//                    .as(StepVerifier::create)
//                    .verifyComplete();

        } catch (Exception error) {
            log.error("Error saving traveltime: " + error);
        }
    }
}
