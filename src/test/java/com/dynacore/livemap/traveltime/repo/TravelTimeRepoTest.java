package com.dynacore.livemap.traveltime.repo;

import com.dynacore.livemap.traveltime.domain.TravelTimeFeatureImpl;
import io.r2dbc.spi.Connection;
import io.r2dbc.spi.ConnectionFactories;
import io.r2dbc.spi.ConnectionFactory;
import io.r2dbc.spi.Row;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.geojson.GeoJsonObject;
import org.geojson.LineString;
import org.geojson.LngLatAlt;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.data.convert.WritingConverter;
import org.springframework.data.r2dbc.core.DatabaseClient;
import org.springframework.data.r2dbc.mapping.OutboundRow;
import org.springframework.data.r2dbc.mapping.SettableValue;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.JavaType;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.type.TypeFactory;
import reactor.test.StepVerifier;

import javax.persistence.Id;
import java.sql.DriverManager;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import static org.junit.Assert.*;
import static org.springframework.data.r2dbc.query.Criteria.where;

public class TravelTimeRepoTest {

  ConnectionFactory connectionFactory =  ConnectionFactories.get("r2dbc:h2:mem://localhost/home/stormraptor/h2db/:///test;MODE=postgresql?options=DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE");
  private final DatabaseClient client = DatabaseClient.create(connectionFactory);

//  file:/Users/foo/data/db

  private TravelTimeRepo repo;

  TravelTimeEntityImpl entityOne,
      entitySameAsOne,
      entitySameAsOneWithNewPubDate,
      entitySameAsOneChangedProperties,
      entityTwo;

  @Before
  public void setup() throws ClassNotFoundException {

    repo  = new TravelTimeRepo(client);


    String pubDate = "2019-10-16T15:52:00Z";
    String retDate = "2019-10-16T16:00:00Z";

    entityOne =
        new TravelTimeEntityImpl(
            null,
            "002",
            "First entity",
            OffsetDateTime.parse(pubDate),
            OffsetDateTime.parse(retDate),
            "type",
            200,
            5,
            100);
    entitySameAsOne =
        new TravelTimeEntityImpl(
            null,
            "002",
            "First entity",
            OffsetDateTime.parse(pubDate),
            OffsetDateTime.parse(retDate),
            "type",
            200,
            5,
            100);

    String newPubDate = "2019-10-16T15:53:00Z";
    entitySameAsOneWithNewPubDate =
        new TravelTimeEntityImpl(
            null,
            "002",
            "First entity",
            OffsetDateTime.parse(newPubDate),
            OffsetDateTime.parse(retDate),
            "type",
            200,
            5,
            100);

    int newLength = 100;
    int newTravelTime = 90;
    int newVelocity = 60;

    entitySameAsOneChangedProperties =
        new TravelTimeEntityImpl(
            null,
            "002",
            "First entity",
            OffsetDateTime.parse(newPubDate),
            OffsetDateTime.parse(retDate),
            "type",
            newLength,
            newTravelTime,
            newVelocity);
    entityTwo =
        new TravelTimeEntityImpl(
            null,
            "009",
            "Second Entity",
            OffsetDateTime.parse(pubDate),
            OffsetDateTime.parse(retDate),
            "type",
            200,
            5,
            100);
  }


  public void dropCreate(DatabaseClient client) {

    String dropTravelTime =  "DROP TABLE IF EXISTS travel_time_entity; ";
    String dropGeometry = "DROP TABLE IF EXISTS geometries; ";
    String createGeometry =      """
                                create table geometries( pk serial not null
                                                        constraint geometries_pk
                                                        primary key,
                                                        id varchar(200),
                                                        type varchar(200),
                                                        geotype varchar(200), 
                                                        geom geometry );
                                """;

    String createTravelTime =
                                """
                                 create table travel_time_entity
                                 (
                                     pkey serial not null
                                         constraint travel_time_entity_pkey
                                             primary key,
                                     id varchar(200),
                                     name varchar(200),
                                     pub_date timestamp with time zone not null,
                                     our_retrieval timestamp with time zone,
                                     type varchar(50),
                                     length smallint
                                         constraint travel_time_entity_length_check
                                             check (length >= '-1'::integer),
                                     velocity smallint
                                         constraint travel_time_entity_velocity_check
                                             check (velocity >= '-1'::integer),
                                     travel_time smallint
                                         constraint travel_time_entity_travel_time_check
                                             check (travel_time >= '-1'::integer),
                                     geometry varchar(200)
                                         constraint travel_time_entity_geometries_id_fk
                                             references geometries (id),
                                     constraint travel_time_entity_id_pub_date_key
                                         unique (id, pub_date)
                                 );
                                """;



    List<String> statements = Arrays.asList(dropTravelTime,  dropGeometry,  createGeometry, createTravelTime);
    statements.forEach(
        it ->
            client
                .execute(it) //
                .fetch() //
                .rowsUpdated() //
                .as(StepVerifier::create) //
                .expectNext(0)
                .verifyComplete());



  }

  @Test
  public void insertEntityOne() {
    dropCreate(client);

    String pubDate = "2019-10-16T15:52:00Z";
    String retDate = "2019-10-16T16:00:00Z";
    TravelTimeEntityImpl entityOne =
        new TravelTimeEntityImpl(
            null,
            "002",
            "First entity",
            OffsetDateTime.parse(pubDate),
            OffsetDateTime.parse(retDate),
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
        .as(StepVerifier::create)
        .expectNext(1)
        .verifyComplete();
  //  client.execute("select id from travel_time_entity").fetch().one().as(StepVerifier::create).expectSubscription().assertNext(map-> assertEquals( "002", map.get("ID"))).verifyComplete();
  }
 // {"type":"Feature","properties":{"Id":"RWS01_MONIBAS_0091hrl0356ra0","Name":"0091hrl0356ra0","Type":"H","Timestamp":"2020-02-07T21:51:00Z","Length":623},
  // "geometry":{"type":"LineString","coordinates":[[4.776645712715283,52.338380232953895],[4.776853788479121,52.33827956952142],[4.77842037340242,52.337460757548655],[4.778671519814815,52.33733621949967],[4.780652279562285,52.336267847567214],[4.782159793662865,52.33546665913931],[4.782751047173977,52.335146118375064],[4.78306134179851,52.33498592177948],[4.78356224185475,52.33472011500613]]}}



  record GeometryEntity(String id, String type, String geotype, LineString geom ) {  }

  @Test
  public void geometryInsertion() {
    dropCreate(client);

//    INSERT INTO geometries VALUES
//    ('Linestring', 'LINESTRING(0 0, 1 1, 2 1, 2 2)'),

    TravelTimeFeatureImpl travelTimeFeature = new TravelTimeFeatureImpl();
    LineString lineString = new LineString(
            new LngLatAlt(4.776645712715283,52.338380232953895),
            new LngLatAlt(4.776853788479121,52.33827956952142),
            new LngLatAlt(4.77842037340242,52.337460757548655),
            new LngLatAlt(4.778671519814815,52.33733621949967),
            new LngLatAlt(4.780652279562285,52.336267847567214),
            new LngLatAlt(4.782159793662865,52.33546665913931));

    var geoEntity = new GeometryEntity("002", "TravelTime", "LineString", lineString);
           // .matching(where("id").is("002"))

    System.out.println( lineString.toString() );

//    client.insert()
//            .into("geometries")
//            .value("id", "002")
//            .value("geom", lineString.toString() )
//            .value("type", "TravelTime")
//            .value("geotype", "LineString")
//            .fetch()
//            .rowsUpdated()
//            .block();

//    var object = client.execute( "select id, type, geotype, geom FROM geometries")
//            .fetch().one()
//            .map(row -> {
//
//                System.out.println( row.get("geom"));
//         //       return new GeometryEntity( "idtest",  "typetest", "geotypetest", new LineString() );
//            //  return row.get("geom");
//              //return new GeometryEntity( (String ) row.get("ID"),  (String )row.get("TYPE"), (String )row.get("GEOTYPE"), new LineString() );
//              return row;
//            }).then().block();

    System.out.println("hello");
  }

  //  assertNext(id-> assertEquals(id, entityOne.getId())
  @Test
  public void isNew() {

    dropCreate(client);

    String pubDate = "2019-10-16T15:52:00Z";
    String retDate = "2019-10-16T16:00:00Z";

    entityOne =
        new TravelTimeEntityImpl(
            null,
            "002",
            "First entity",
            OffsetDateTime.parse(pubDate),
            OffsetDateTime.parse(retDate),
            "type",
            200,
            5,
            100);
    entitySameAsOne =
        new TravelTimeEntityImpl(
            null,
            "002",
            "First entity",
            OffsetDateTime.parse(pubDate),
            OffsetDateTime.parse(retDate),
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
        .as(StepVerifier::create)
        .expectNext(1)
        .verifyComplete();

    Boolean isNew =
        Optional.ofNullable(repo.isNew(entitySameAsOne).block())
            .orElseThrow(NoSuchElementException::new);
    assertFalse(isNew);

    String newPubDate = "2019-10-16T15:53:00Z";
    entitySameAsOneWithNewPubDate =
        new TravelTimeEntityImpl(
            null,
            "002",
            "First entity",
            OffsetDateTime.parse(newPubDate),
            OffsetDateTime.parse(retDate),
            "type",
            200,
            5,
            100);

    isNew =
        Optional.ofNullable(repo.isNew(entitySameAsOneWithNewPubDate).block())
            .orElseThrow(NoSuchElementException::new);

    assertTrue(isNew);
  }

  @Test
  public void isTimeZoneCorrectlyStoredAndRetrieved() {
    dropCreate(client);

    String pubDate = "2019-10-16T15:52:00Z";
    String retDate = "2019-10-16T16:00:00Z";
    TravelTimeEntityImpl entityOne =
        new TravelTimeEntityImpl(
            null,
            "002",
            "First entity",
            OffsetDateTime.parse(pubDate),
            OffsetDateTime.parse(retDate),
            "type",
            200,
            5,
            100);

    client.insert().into(TravelTimeEntityImpl.class).using(entityOne).fetch().rowsUpdated().block();

    TravelTimeEntityImpl entityTwo = repo.getLatest("002").block();

    assert entityTwo != null;

    System.out.println(entityOne.getPubDate());
    System.out.println(entityTwo.getPubDate());

    assertTrue(entityOne.getPubDate().isEqual(entityTwo.getPubDate()));
  }

  @Test
  public void saveOneThenIgnoreSame() {

    dropCreate(client);
    repo.save(entityOne).block();

    client
        .select()
        .from(TravelTimeEntityImpl.class)
        .fetch()
        .first()
        .as(StepVerifier::create)
        .consumeNextWith(
            retrieved -> {
              assertSame(retrieved.getPkey(), 1);
              assertEquals(retrieved.getId(), "002");
              assertEquals(retrieved.getName(), "First entity");
              assertTrue(
                  retrieved.getPubDate().isEqual(OffsetDateTime.parse("2019-10-16T15:52:00Z")));
              assertTrue(
                  retrieved
                      .getOurRetrieval()
                      .isEqual(OffsetDateTime.parse("2019-10-16T16:00:00Z")));
              assertEquals(retrieved.getType(), "type");
              assertEquals(200, retrieved.getLength().intValue());
              assertEquals(5, retrieved.getTravel_time().intValue());
              assertEquals(100, retrieved.getVelocity().intValue());
            })
        .verifyComplete();

    repo.save(entityOne).block();

    client
        .select()
        .from(TravelTimeEntityImpl.class)
        .fetch()
        .all()
        .as(StepVerifier::create)
        .expectNextCount(1)
        .verifyComplete();
  }

  @Test
  public void getLastStored() {
    dropCreate(client);
    insertEntityOne();
    repo.getLatest(entityOne.getId())
        .as(StepVerifier::create)
        .expectNext(entityOne)
        .verifyComplete();
  }
}
