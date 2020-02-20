package com.dynacore.livemap;

import com.dynacore.livemap.configuration.database.postgiscodec.postgis.Geometry;
import com.dynacore.livemap.configuration.database.postgiscodec.postgis.Point;
import com.dynacore.livemap.configuration.database.postgiscodec.postgis.PostGisCodecRegistrar;
import io.r2dbc.postgresql.PostgresqlConnectionConfiguration;
import io.r2dbc.postgresql.PostgresqlConnectionFactory;

import io.r2dbc.spi.ConnectionFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineString;
import org.springframework.data.r2dbc.core.DatabaseClient;
import reactor.test.StepVerifier;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

class PostGisCodecIntegrationTest {

    @Test
    void shouldRegisterCodec() {

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

        DatabaseClient   client = DatabaseClient.create(connectionFactory);


      //  client.execute("DROP TABLE IF EXISTS codec_geometry_test;CREATE TABLE codec_geometry_test (my_value geometry(POINT,4326));").fetch().rowsUpdated().block();

   //     client.execute("INSERT INTO codec_geometry_test(name, geom) VALUES('point', ST_GeomFromText('POINT(-73.985744 40.748549)',4326));").fetch().rowsUpdated().block();


      //  System.out.println(geometry.getDimension());

        client.execute("SELECT * FROM codec_geometry_test")
                .map((row, rowMetadata) -> row.get(1))
                .one()
                .cast(Geometry.class)
                .as(StepVerifier::create)
                .consumeNextWith(geometry -> {
                    assertThat(geometry.getDimension()).isEqualTo(2);
                    assertThat(geometry.getType()).isEqualTo(Geometry.POINT);
                    assertThat(geometry).isInstanceOf(Point.class);
                    final Point point = (Point) geometry;
                    assertThat(point.getX()).isCloseTo(-73.985744, within(0.0000001));
                    assertThat(point.getY()).isCloseTo(40.748549, within(0.0000001));
                })
                .verifyComplete();
    }

    @Test
    void shouldRegisterCodecLineString() {

        ConnectionFactory connectionFactory =
                new PostgresqlConnectionFactory(
                        PostgresqlConnectionConfiguration.builder()
                                .host("localhost")
                                .port(5432) // optional, defaults to 5432
                                .username("postgres")
                                .password("admin")
                                .database("postgisdb")
                                .build());

        DatabaseClient   client = DatabaseClient.create(connectionFactory);


     //   client.execute("DROP TABLE IF EXISTS codec_geometry_test;CREATE TABLE codec_geometry_test (my_value geometry(LINESTRING,4326));").fetch().rowsUpdated().block();

      //  client.execute("INSERT INTO codec_geometry_test VALUES(ST_GeomFromText('LINESTRING(77.29 29.07,77.42 29.26,77.27 29.31,77.29 29.07)',4326));").fetch().rowsUpdated().block();


        client.execute("SELECT * FROM codec_geometry_test")
                .map((row, rowMetadata) -> row.get(0))
                .one()
                .cast(Geometry.class)
                .as(StepVerifier::create)
                .consumeNextWith(geometry -> {
//                    assertThat(geometry.getDimension()).isEqualTo(2);
                    assertThat(geometry.getType()).isEqualTo(Geometry.LINESTRING);
                    assertThat(geometry).isInstanceOf(LineString.class);
//                    final Point point = (Point) geometry;
//                    assertThat(point.getX()).isCloseTo(-73.985744, within(0.0000001));
//                    assertThat(point.getY()).isCloseTo(40.748549, within(0.0000001));
                })
                .verifyComplete();
    }


}
