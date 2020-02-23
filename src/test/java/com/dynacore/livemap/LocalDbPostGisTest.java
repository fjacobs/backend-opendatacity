package com.dynacore.livemap;

import com.dynacore.livemap.configuration.database.postgiscodec.PostGisCodecRegistrar;
import com.dynacore.livemap.core.repository.EntityMapper;
import com.dynacore.livemap.core.repository.GeometryEntity;
import com.dynacore.livemap.traveltime.domain.TravelTimeFeatureImpl;
import io.r2dbc.postgresql.PostgresqlConnectionConfiguration;
import io.r2dbc.postgresql.PostgresqlConnectionFactory;
import io.r2dbc.spi.ConnectionFactory;
import org.junit.Test;
import org.locationtech.jts.geom.Geometry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.r2dbc.core.DatabaseClient;
import reactor.core.publisher.Flux;

public class LocalDbPostGisTest {

  @Autowired DatabaseClient client;


  @Test
  public void geometryInsertion() {

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
            "DROP TABLE IF EXISTS codec_geometry;CREATE TABLE codec_geometry ( pkey serial not null constraint codec_geometry_pk primary key, id varchar(200), geo_type varchar(200), data_type varchar(200), geom geometry);")
        .fetch()
        .rowsUpdated()
        .block();

    org.geojson.LineString lineString =
        new org.geojson.LineString(
            new org.geojson.LngLatAlt(4.776645712715283, 52.338380232953895),
            new org.geojson.LngLatAlt(4.776853788479121, 52.33827956952142),
            new org.geojson.LngLatAlt(4.77842037340242, 52.337460757548655),
            new org.geojson.LngLatAlt(4.778671519814815, 52.33733621949967),
            new org.geojson.LngLatAlt(4.780652279562285, 52.336267847567214),
            new org.geojson.LngLatAlt(4.782159793662865, 52.33546665913931));

    TravelTimeFeatureImpl feature = new TravelTimeFeatureImpl();
    feature.setId("idxx");
    feature.setLength(100);
    feature.getGenericGeoJson().setGeometry(lineString);
    GeometryEntity entity = EntityMapper.geometryEntityConvertor(feature);

    client
        .execute(
            "INSERT INTO codec_geometry(id, geo_type, data_type, geom) VALUES( 'idx', 'geoType', 'travel_time', ST_GeomFromText('"
                + entity.geom().toString()
                + "',4326));")
        .fetch()
        .rowsUpdated()
        .block();

    Geometry line = client.execute("SELECT * FROM codec_geometry where data_type=" + "'travel_time'")
            .map((row, rowMetadata) -> row.get(4))
            .one()
            .cast(Geometry.class)
            .doOnNext(System.out::println)

            .block();
  }
}
