package com.dynacore.livemap.traveltime.repo;

import io.r2dbc.spi.ConnectionFactories;
import org.junit.Before;
import org.junit.Test;
import org.springframework.data.r2dbc.core.DatabaseClient;
import reactor.test.StepVerifier;

import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.Assert.*;

public class TravelTimeRepoTest {

  private DatabaseClient client =
      DatabaseClient.create(
          ConnectionFactories.get(
              "r2dbc:h2:mem:///test;MODE=postgresql?options=DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE"));
  private TravelTimeRepo repo = new TravelTimeRepo(client);

  TravelTimeEntity entityOne,
      entitySameAsOne,
      entitySameAsOneWithNewPubDate,
      entitySameAsOneChangedProperties,
      entityTwo;

  @Before
  public void setup() {
    String pubDate = "2019-10-16T15:52:00Z";
    String retDate = "2019-10-16T16:00:00Z";

    entityOne =
        new TravelTimeEntity(
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
        new TravelTimeEntity(
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
        new TravelTimeEntity(
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
        new TravelTimeEntity(
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
        new TravelTimeEntity(
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

    List<String> statements =
        Arrays.asList( //
            "DROP TABLE IF EXISTS travel_time_entity;",
            "CREATE TABLE TRAVEL_TIME_ENTITY\n"
                + "(\n"
                + "    pkey SERIAL PRIMARY KEY,\n"
                + "    id                         VARCHAR(200),\n"
                + "    name                       VARCHAR(200),\n"
                + "    pub_date                   TIMESTAMP WITH TIME ZONE  NOT NULL,\n"
                + "    our_retrieval TIMESTAMP WITH TIME ZONE  NOT NULL,\n"
                + "    type                       VARCHAR(50),\n"
                + "    length                     SMALLINT CHECK (length >= -1),\n"
                + "    velocity                   SMALLINT CHECK (velocity >= -1),\n"
                + "    travel_time                SMALLINT CHECK (travel_time >= -1),\n"
                + "    unique (id, pub_date)\n"
                + ");");



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
    TravelTimeEntity entityOne =
        new TravelTimeEntity(
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
        .into(TravelTimeEntity.class)
        .using(entityOne)
        .fetch()
        .rowsUpdated()
        .as(StepVerifier::create)
        .expectNext(1)
        .verifyComplete();
  }

  @Test
  public void isNew() {

    dropCreate(client);

    String pubDate = "2019-10-16T15:52:00Z";
    String retDate = "2019-10-16T16:00:00Z";

    entityOne =
        new TravelTimeEntity(
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
        new TravelTimeEntity(
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
        .into(TravelTimeEntity.class)
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
        new TravelTimeEntity(
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
    TravelTimeEntity entityOne =
        new TravelTimeEntity(
            null,
            "002",
            "First entity",
            OffsetDateTime.parse(pubDate),
            OffsetDateTime.parse(retDate),
            "type",
            200,
            5,
            100);

    client.insert().into(TravelTimeEntity.class).using(entityOne).fetch().rowsUpdated().block();

    TravelTimeEntity entityTwo = repo.getLatest("002").block();

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
        .from(TravelTimeEntity.class)
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
        .from(TravelTimeEntity.class)
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
