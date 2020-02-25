package com.dynacore.livemap;

import com.dynacore.livemap.configuration.database.postgiscodec.PostGisCodecRegistrar;
import com.dynacore.livemap.core.Direction;
import com.dynacore.livemap.core.model.TrafficFeatureImpl;
import com.dynacore.livemap.traveltime.repo.TravelTimeRepo;
import io.r2dbc.postgresql.PostgresqlConnectionConfiguration;
import io.r2dbc.postgresql.PostgresqlConnectionFactory;

import io.r2dbc.spi.ConnectionFactory;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.Point;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.r2dbc.core.DatabaseClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Hooks;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.time.OffsetDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

class PostGisCodecIntegrationTest {

  @Autowired DatabaseClient client;

  @Test
  void testPoint() {

    ConnectionFactory connectionFactory =
        new PostgresqlConnectionFactory(
            PostgresqlConnectionConfiguration.builder()
                .host("localhost")
                .port(5432) // optional, defaults to 5432
                .username("postgres")
                .password("admin")
                .database("postgisdb")
                .codecRegistrar(new PostGisCodecRegistrar())
                .build());

    DatabaseClient client = DatabaseClient.create(connectionFactory);

    client
        .execute(
            "DROP TABLE IF EXISTS codec_geometry_test;CREATE TABLE codec_geometry_test ( name varchar(200), geom geometry(POINT,4326));")
        .fetch()
        .rowsUpdated()
        .block();
    client
        .execute(
            "INSERT INTO codec_geometry_test(name, geom) VALUES('point', ST_GeomFromText('POINT(-73.985744 40.748549)',4326));")
        .fetch()
        .rowsUpdated()
        .block();
    client
        .execute("SELECT * FROM codec_geometry_test")
        .map((row, rowMetadata) -> row.get(1))
        .one()
        .cast(Geometry.class)
        .as(StepVerifier::create)
        .assertNext(
            geometry -> {
              assertThat(geometry).isInstanceOf(Point.class);
              final Point point = (Point) geometry;
              System.out.println(point);
              assertThat(point.getX()).isCloseTo(-73.985744, within(0.0000001));
              assertThat(point.getY()).isCloseTo(40.748549, within(0.0000001));
            })
        .verifyComplete();
  }

  @Test
  void shouldRegisterCodecLineString() {
    Hooks.onOperatorDebug();
    ConnectionFactory connectionFactory =
        new PostgresqlConnectionFactory(
            PostgresqlConnectionConfiguration.builder()
                .host("localhost")
                .port(5432) // optional, defaults to 5432
                .username("postgres")
                .password("admin")
                .database("postgisdb")
                .codecRegistrar(new PostGisCodecRegistrar())
                .build());

    DatabaseClient client = DatabaseClient.create(connectionFactory);

    client
        .execute(
            "DROP TABLE IF EXISTS codec_geometry_test;CREATE TABLE codec_geometry_test (my_value geometry(LINESTRING,4326));")
        .fetch()
        .rowsUpdated()
        .block();
    client
        .execute(
            "INSERT INTO codec_geometry_test VALUES(ST_GeomFromText('LINESTRING(77.29 29.07,77.42 29.26)',4326));")
        .fetch()
        .rowsUpdated()
        .block();

    client
        .execute("SELECT * FROM codec_geometry_test")
        .map((row, rowMetadata) -> row.get(0))
        .one()
        .cast(Geometry.class)
        .as(StepVerifier::create)
        .assertNext(x -> Assert.assertEquals("LINESTRING (77.29 29.07, 77.42 29.26)", x.toString()))
        .verifyComplete();
  }

  record idLoc(String id, Geometry geom) {};

  @Test
  void getLocationWithinRange() {
    Hooks.onOperatorDebug();
    ConnectionFactory connectionFactory =
        new PostgresqlConnectionFactory(
            PostgresqlConnectionConfiguration.builder()
                .host("localhost")
                .port(5432) // optional, defaults to 5432
                .username("postgres")
                .password("admin")
                .database("postgisdb")
                .codecRegistrar(new PostGisCodecRegistrar())
                .build());

    DatabaseClient client = DatabaseClient.create(connectionFactory);

    client
        .execute(
            "DROP TABLE IF EXISTS codec_geometry_test;CREATE TABLE codec_geometry_test (geom geometry);")
        .fetch()
        .rowsUpdated()
        .block();
    client
        .execute(
            // Binary in 'VALUES' below is the same as using:
            // ST_GeomFromText('LINESTRING(4.93074407259659 52.305265175062,4.93308922656803
            // 52.3030160425161)',4326)
            "INSERT INTO codec_geometry_test VALUES('0102000020E6100000020000002DFE62F914B91340E3BFE3ED12274A40427001BE7BBB13408F62CC3AC9264A40');")
        .fetch()
        .rowsUpdated()
        .then()
        .flatMap(
            v ->
                client
                    .execute(
                        "INSERT INTO codec_geometry_test VALUES(ST_GeomFromText('LINESTRING(77.29 29.07,77.42 29.26)',4326));")
                    .fetch()
                    .rowsUpdated())
        .block();

    client
        .execute(
            "SELECT geom FROM codec_geometry_test where geom && ST_MakeEnvelope(4.577447733334896,52.28670934090339,  5.22255118792474, 52.45440226066263,  4326)")
        .map((row, rowMetadata) -> new idLoc("id", (Geometry) row.get(0)))
        .all()
        .as(StepVerifier::create)
        .expectNextCount(1)
        .verifyComplete();
  }

  @Test
  void getDecreasing() {
    Hooks.onOperatorDebug();
    ConnectionFactory connectionFactory =
        new PostgresqlConnectionFactory(
            PostgresqlConnectionConfiguration.builder()
                .host("localhost")
                .port(5432) // optional, defaults to 5432
                .username("postgres")
                .password("admin")
                .database("test_db")
                .codecRegistrar(new PostGisCodecRegistrar())
                .build());
//    2020-02-24 15:00:01.000000

    DatabaseClient client = DatabaseClient.create(connectionFactory);
    TravelTimeRepo repo = new TravelTimeRepo(client);
    repo.getReplayData(OffsetDateTime.parse("2020-02-24T15:00:01+00:00"), Direction.BACKWARD)
            .doOnNext(System.out::println)
            .blockLast();
  }
}
