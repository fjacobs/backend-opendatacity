package com.dynacore.livemap.guidancesign.repo;

import com.dynacore.livemap.guidancesign.domain.*;
import io.r2dbc.spi.ConnectionFactories;
import io.r2dbc.spi.ConnectionFactory;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.r2dbc.core.DatabaseClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Hooks;
import reactor.test.StepVerifier;

import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GuidanceSignRepoTest {

  static DatabaseClient client;
  static GuidanceSignRepo repo;
  static GuidanceSignFeatureImpl externalFeature1;
  static GuidanceSignFeatureImpl externalFeature2;

  static GuidanceSignAggregate aggregate1;
  static GuidanceSignAggregate aggregate2;

  @BeforeAll
  @Test
  public static void initDb() {

    ConnectionFactory connectionFactory =
        ConnectionFactories.get(
            "r2dbc:h2:mem:///test;MODE=postgresql?options=DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE");

    client = DatabaseClient.create(connectionFactory);
    repo = new GuidanceSignRepo(client);

    externalFeature1 = new GuidanceSignFeatureImpl();
    externalFeature1.setId("id2");
    externalFeature1.setName("name2");
    externalFeature1.setState("ok");
    externalFeature1.setPubDate(OffsetDateTime.parse("2025-01-01T00:00:00Z"));
    externalFeature1.setOurRetrieval(OffsetDateTime.now());
    externalFeature1.setType("Type");
    externalFeature1
        .getInnerDisplays()
        .add(
            new InnerDisplayModel.Builder()
                .id("innerid1")
                .outputDescription("outputdescr1")
                .description("description1")
                .output("outputtest1")
                .type("typetest1")
                .removed(false)
                .build());
    externalFeature1
        .getInnerDisplays()
        .add(
            new InnerDisplayModel.Builder()
                .id("innerid2")
                .outputDescription("outputdescr2")
                .description("description2")
                .output("outputtest2")
                .type("typetest2")
                .removed(false)
                .build());
    aggregate1 = new GuidanceSignAggregate(externalFeature1);

    externalFeature2 = new GuidanceSignFeatureImpl();
    externalFeature2.setId("id1");
    externalFeature2.setName("name1");
    externalFeature2.setState("ok");
    externalFeature2.setPubDate(OffsetDateTime.parse("2025-12-12T12:12:12Z"));
    externalFeature2.setOurRetrieval(OffsetDateTime.now());
    externalFeature2.setType("Type");

    externalFeature2
        .getInnerDisplays()
        .add(
            new InnerDisplayModel.Builder()
                .id("innerid3")
                .outputDescription("outputdescr1")
                .description("description3")
                .output("outputtest3")
                .type("typetest1")
                .removed(false)
                .build());
    externalFeature2
        .getInnerDisplays()
        .add(
            new InnerDisplayModel.Builder()
                .id("innerid4")
                .outputDescription("outputdescr4")
                .description("description4")
                .output("outputtest4")
                .type("typetest4")
                .removed(false)
                .build());
    aggregate2 = new GuidanceSignAggregate(externalFeature2);
  }

  @BeforeEach
  public void createTables() {
    List<String> statements =
        Arrays.asList(
            "DROP TABLE IF EXISTS INNER_DISPLAY_ENTITY;",
            "DROP TABLE IF EXISTS GUIDANCE_SIGN_ENTITY;",
            "CREATE TABLE GUIDANCE_SIGN_ENTITY\n"
                + "(\n"
                + "\tpkey serial not null\n"
                + "\t\tconstraint guidance_sign_entity_pk\n"
                + "\t\t\tprimary key,\n"
                + "\tid varchar default 200,\n"
                + "\tname varchar default 200,\n"
                + "\tpub_date timestamp with time zone not null,\n"
                + "\tour_retrieval timestamp with time zone,\n"
                + "\tremoved boolean,\n"
                + "\tstate varchar default 200,\n"
                + "\tunique (id, pub_date)\n"
                + "); ",
            "CREATE TABLE INNER_DISPLAY_ENTITY\n"
                + "(\n"
                + "\tpkey serial not null\n"
                + "\t\tconstraint inner_display_entity_pk\n"
                + "\t\t\tprimary key,\n"
                + "\tid varchar,\n"
                + "\toutput_description varchar default 200,\n"
                + "\tdescription varchar default 200,\n"
                + "\ttype varchar default 200,\n"
                + "\toutput varchar default 200,\n"
                + "\tfk integer not null\n"
                + "\t\tconstraint inner_display_entity_guidance_sign_entity_pkey_fk\n"
                + "\t\t\treferences guidance_sign_entity\n"
                + ");\n"
                + "\n");

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
  void save() {
    Hooks.onOperatorDebug();

    repo.save(aggregate1).log().block();
    repo.save(aggregate2).log().block();

    client
        .select()
        .from(GuidanceSignEntity.class)
        .fetch()
        .all()
        .as(StepVerifier::create)
        .assertNext(
            aggr -> Assert.assertEquals(aggr.getId(), aggregate1.getGuidanceSignEntity().getId()))
        .assertNext(
            aggr -> Assert.assertEquals(aggr.getId(), aggregate2.getGuidanceSignEntity().getId()))
        .verifyComplete();

    client
            .select()
            .from(InnerDisplayEntity.class)
            .fetch()
            .all()
            .as(StepVerifier::create)
            .assertNext(x-> assertEquals("innerid1",x.getId() ))
            .assertNext(x->  assertEquals( "description2", x.getDescription()))
            .assertNext(x-> assertEquals( "outputtest3",x.getOutput()))
            .assertNext(x->  assertEquals("outputdescr4",x.getOutputDescription() ))
            .verifyComplete();
  }

  @Test
  void failOnUniqueIdAndPubdateCombi() {
    Hooks.onOperatorDebug();
    repo.save(aggregate1).log().block();

    GuidanceSignEntity x = new GuidanceSignEntity();
    x.setId(aggregate1.getId());
    x.setPubDate(aggregate1.getPubDate());
    InnerDisplayEntity inner =
        new InnerDisplayEntity.Builder()
            .innerDisplayId("innerid2")
            .outputDescription("outputdescr1")
            .description("description test 2")
            .output("outputtest2")
            .type("typetest2")
            .build();

    var aggregate = new GuidanceSignAggregate(x);
    aggregate.setInnerDisplayEntities(Flux.just(inner));

    repo.save(aggregate).as(StepVerifier::create).expectError(DataIntegrityViolationException.class).verify();
  }

  @Test
  void isNew() {
    save();
    repo.isNew(aggregate1).as(StepVerifier::create).expectNext(Boolean.FALSE);
    var x = new GuidanceSignEntity();
    x.setId("id1");
    x.setPubDate(OffsetDateTime.now());
    repo.isNew(aggregate1).as(StepVerifier::create).expectNext(Boolean.TRUE);
  }

  @Test
  void getAllAscending() {
    Hooks.onOperatorDebug();

    repo.save(aggregate1).log().block();
    repo.save(aggregate2).log().block();

    repo.getAllAscending().count().doOnNext(System.out::println).block();

            repo.getAllAscending()
                .as(StepVerifier::create)
                 .consumeNextWith(entityX-> assertEquals(aggregate1.getId(), entityX.getId()))
                 .consumeNextWith(entityX-> assertEquals(aggregate2.getId(), entityX.getId()))
                .verifyComplete();

  }
}
