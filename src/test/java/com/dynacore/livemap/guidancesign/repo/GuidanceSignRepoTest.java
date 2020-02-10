package com.dynacore.livemap.guidancesign.repo;

import com.dynacore.livemap.configuration.database.PostgresConfiguration;
import com.dynacore.livemap.guidancesign.domain.GuidanceSignEntity;
import com.dynacore.livemap.guidancesign.domain.GuidanceSignFeature;
import com.dynacore.livemap.guidancesign.domain.InnerDisplayModel;
import com.dynacore.livemap.testing.database.ExternalDatabase;
import com.dynacore.livemap.testing.database.PostgresTestSupport;
import io.r2dbc.spi.ConnectionFactories;
import io.r2dbc.spi.ConnectionFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.r2dbc.core.DatabaseClient;
import reactor.core.publisher.Hooks;
import reactor.test.StepVerifier;

import java.time.OffsetDateTime;

import static io.r2dbc.spi.ConnectionFactoryOptions.*;
import static io.r2dbc.spi.ConnectionFactoryOptions.DATABASE;

class GuidanceSignRepoTest {

  public static final ExternalDatabase database = PostgresTestSupport.database();


  static DatabaseClient client;

  @BeforeAll
  @Test
  static void initDb() {
    ConnectionFactory connectionFactory = ConnectionFactories.get("r2dbc:h2:mem:///test;MODE=postgresql?options=DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE");
    client = DatabaseClient.create( connectionFactory);
    client
        .execute(
            "create table public.guidance_sign\n"
                + "(\n"
                + "\tpkey serial not null\n"
                + "\t\tconstraint guidance_sign_pk\n"
                + "\t\t\tprimary key,\n"
                + "\tid varchar default 200,\n"
                + "\tname varchar default 200,\n"
                + "\tpub_date timestamp with time zone not null,\n"
                + "\tretrieved_from_third_party timestamp with time zone,\n"
                + "\tremoved boolean,\n"
                + "\tstate varchar default 200,\n"
                + "\tconstraint guidance_sign_id_pub_date_key\n"
                + "\t\tunique (id, pub_date)\n"
                + ");\n")
        .fetch()
        .rowsUpdated()
        .as(StepVerifier::create)
        .expectNextCount(1)
        .verifyComplete();

    client
        .execute(
            "create table public.inner_display_entity\n"
                + "(\n"
                + "\tpkey serial not null\n"
                + "\t\tconstraint guidance_inner_display_guidance_sign_pkey_fk\n"
                + "\t\t\treferences public.guidance_sign,\n"
                + "\tid varchar,\n"
                + "\toutput_description varchar default 200,\n"
                + "\tdescription varchar default 200,\n"
                + "\ttype varchar default 200,\n"
                + "\toutput varchar default 200\n"
                + ");\n")
        .fetch()
        .rowsUpdated()
        .as(StepVerifier::create)
        .expectNextCount(1)
        .verifyComplete();
  }

  @Test
  void save() {
    GuidanceSignRepo repo = new GuidanceSignRepo(client);
    GuidanceSignFeature testWidget = new GuidanceSignFeature();
    testWidget.setId("id1");
    testWidget.setName("name1");
    testWidget.setState("ok");
    testWidget.setPubDate(OffsetDateTime.now());
    testWidget.setOurRetrieval(OffsetDateTime.now());
    testWidget.setType("Type");

    testWidget
        .getInnerDisplays()
        .add(
            new InnerDisplayModel.Builder()
                .id("innerid1")
                .outputDescription("outputdescr1")
                .description("description test 1")
                .output("outputtest1")
                .type("typetest1")
                .removed(false)
                .build());
    testWidget
        .getInnerDisplays()
        .add(
            new InnerDisplayModel.Builder()
                .id("innerid1")
                .outputDescription("outputdescr1")
                .description("description test 2")
                .output("outputtest2")
                .type("typetest2")
                .removed(false)
                .build());

    GuidanceSignEntity entity = new GuidanceSignEntity(testWidget);
    Hooks.onOperatorDebug();
    repo.save(entity).block();
  }
}
