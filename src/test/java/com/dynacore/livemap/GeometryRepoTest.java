package com.dynacore.livemap;

import com.dynacore.livemap.guidancesign.domain.GuidanceSignEntity;
import com.dynacore.livemap.guidancesign.domain.InnerDisplayEntity;
import com.dynacore.livemap.traveltime.domain.TravelTimeFeatureImpl;
import com.dynacore.livemap.traveltime.repo.TravelTimeEntityImpl;
import com.dynacore.livemap.traveltime.repo.TravelTimeRepoTest;
import io.r2dbc.postgresql.PostgresqlConnectionConfiguration;
import io.r2dbc.postgresql.PostgresqlConnectionFactory;
import io.r2dbc.spi.ConnectionFactory;
import org.geojson.LineString;
import org.geojson.LngLatAlt;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.context.annotation.Bean;
import org.springframework.data.r2dbc.convert.R2dbcCustomConversions;
import org.springframework.data.r2dbc.core.DatabaseClient;
import org.springframework.data.r2dbc.mapping.SettableValue;
import reactor.test.StepVerifier;

import javax.persistence.Converter;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
// import org.h2gis.ext.H2GISExtension;

public class GeometryRepoTest {

//  DatabaseClient client;
//
//  @BeforeAll
//  public void init() {
//    ConnectionFactory connectionFactory =
//        new PostgresqlConnectionFactory(
//            PostgresqlConnectionConfiguration.builder()
//                .host("localhost")
//                .port(5432) // optional, defaults to 5432
//                .username("postgres")
//                .password("admin")
//                .database("postgisdb")
//                .build());
//
//    client = DatabaseClient.create(connectionFactory);
//  }

  @Test
  public void dropCreate() {
//      init();
//
//
//      String dropTravelTime =  "DROP TABLE IF EXISTS gistest.travel_time_entity; ";
//      String dropGeometry =    "DROP TABLE IF EXISTS geometries; ";
//      String createGeometry =      """
//                                create table geometries
//                                (
//                                    pk serial not null
//                                        constraint geometries_pk
//                                            primary key,
//                                    type varchar(200),
//                                    id varchar(200),
//                                    geom geometry,
//                                    geotype varchar(50)
//                                );
//                                  create unique index geometries_id_geom_uindex
//                                  on gistest.geometries (id, geom);
//
//                                  create unique index geometries_id_uindex
//                                  on gistest.geometries (id);
//                                """;
//
//      String createTravelTime =
//              """
//                    create table gistest.travel_time_entity
//                    (
//                        pkey serial not null
//                            constraint travel_time_entity_pkey
//                                primary key,
//                        id varchar(200),
//                        name varchar(200),
//                        pub_date timestamp with time zone not null,
//                        our_retrieval timestamp with time zone,
//                        type varchar(50),
//                        length smallint
//                            constraint travel_time_entity_length_check
//                                check (length >= '-1'::integer),
//                        velocity smallint
//                            constraint travel_time_entity_velocity_check
//                                check (velocity >= '-1'::integer),
//                        travel_time smallint
//                            constraint travel_time_entity_travel_time_check
//                                check (travel_time >= '-1'::integer),
//                        geometry varchar(200)
//                            constraint travel_time_entity_geometries_id_fk
//                                references gistest.geometries (id),
//                        constraint travel_time_entity_id_pub_date_key
//                            unique (id, pub_date)
//                    );
//              """;
//
//      List<String> statements = Arrays.asList(dropTravelTime,  dropGeometry,  createGeometry, createTravelTime);
//      statements.forEach(
//              it ->
//                      client
//                              .execute(it) //
//                              .fetch() //
//                              .rowsUpdated().block());
  }



//19231	RWS01_MONIBAS_0041hrr0118ra0	0041hrr0118ra0	2020-02-08 23:31:01.000000		H	691	113	22


    @Test
    public void insertEntityOne() {
      DatabaseClient client = null;
        String pubDate = "2019-10-16T15:52:00Z";
        TravelTimeEntityImpl entityOne =
                new TravelTimeEntityImpl(
                        null,
                        "99999",
                        "First entity",
                        OffsetDateTime.parse(pubDate),
                       null,
                        "type",
                        200,
                        5,
                        100);

        client
                .insert()
                .into(TravelTimeEntityImpl.class)
                .using(entityOne)
                .fetch()
                .rowsUpdated()
//                .then(
//                        databaseClient
//                                .insert()
//                                .into(InnerDisplayEntity.class)
//                                .using(aggregate.getInnerDisplayEntities())
//                                .fetch()
//                                .all()
//                                .then())
//                .then();

                .as(StepVerifier::create)
                .expectNext(0)
                .verifyComplete();


//        return databaseClient
//                .insert()
//                .into(GuidanceSignEntity.class)
//                .using(aggregate.getGuidanceSignEntity())
//                .map(
//                        (row, rowMetadata) -> {
//                            aggregate.setFk(row.get("pkey", Integer.class));
//                            return row;
//                        })
//                .one()
//                .then(
//                        databaseClient
//                                .insert()
//                                .into(InnerDisplayEntity.class)
//                                .using(aggregate.getInnerDisplayEntities())
//                                .fetch()
//                                .all()
//                                .then())
//                .then();


      //    client.execute("select id from travel_time_entity").fetch().one().as(StepVerifier::create).expectSubscription().assertNext(map-> assertEquals( "99999", map.get("ID"))).verifyComplete();
    }
    // {"type":"Feature","properties":{"Id":"RWS01_MONIBAS_0091hrl0356ra0","Name":"0091hrl0356ra0","Type":"H","Timestamp":"2020-02-07T21:51:00Z","Length":623},
    // "geometry":{"type":"LineString","coordinates":[[4.776645712715283,52.338380232953895],[4.776853788479121,52.33827956952142],[4.77842037340242,52.337460757548655],[4.778671519814815,52.33733621949967],[4.780652279562285,52.336267847567214],[4.782159793662865,52.33546665913931],[4.782751047173977,52.335146118375064],[4.78306134179851,52.33498592177948],[4.78356224185475,52.33472011500613]]}}


}
