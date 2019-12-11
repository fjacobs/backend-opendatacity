package com.dynacore.livemap.traveltime.repo;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import javax.sql.DataSource;

import com.dynacore.livemap.configuration.PostgresConfig;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.data.r2dbc.core.DatabaseClient;
import org.springframework.data.r2dbc.core.FetchSpec;

import com.dynacore.livemap.testing.database.AbstractDatabaseClientIntegrationTests;
import com.dynacore.livemap.testing.database.ExternalDatabase;
import com.dynacore.livemap.testing.database.PostgresTestSupport;

import io.r2dbc.spi.ConnectionFactory;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;


public class TravelTimeRepoTest extends AbstractDatabaseClientIntegrationTests {

    private DatabaseClient client = DatabaseClient.create(createConnectionFactory());
    private TravelTimeRepo repo= new TravelTimeRepo(client);

    TravelTimeEntity entityOne, entitySameAsOne, entitySameAsOneWithNewPubDate, entitySameAsOneChangedProperties, entityTwo;

    public static final ExternalDatabase database = PostgresTestSupport.database();

    @Override
    protected DataSource createDataSource() {
        return PostgresTestSupport.createDataSource(database);
    }

    @Override
    protected ConnectionFactory createConnectionFactory() {
        return new PostgresConfig().connectionFactory();
    }

    @Before
    public void setup() {

        String pubDate = "2019-10-16T15:52:00Z";
        String retDate = "2019-10-16T16:00:00Z";

        entityOne = new TravelTimeEntity(null, "002", "First entity", OffsetDateTime.parse(pubDate), OffsetDateTime.parse(retDate), "type", 200, 5, 100);
        entitySameAsOne = new TravelTimeEntity(null, "002", "First entity", OffsetDateTime.parse(pubDate), OffsetDateTime.parse(retDate), "type", 200, 5, 100);

        String newPubDate = "2019-10-16T15:53:00Z";
        entitySameAsOneWithNewPubDate = new TravelTimeEntity(null, "002", "First entity", OffsetDateTime.parse(newPubDate), OffsetDateTime.parse(retDate), "type", 200, 5, 100);

        int newLength = 100;
        int newTravelTime = 90;
        int newVelocity = 60;

        entitySameAsOneChangedProperties = new TravelTimeEntity(null, "002", "First entity", OffsetDateTime.parse(newPubDate), OffsetDateTime.parse(retDate), "type", newLength, newTravelTime, newVelocity);
        entityTwo = new TravelTimeEntity(null, "009", "Second Entity", OffsetDateTime.parse(pubDate), OffsetDateTime.parse(retDate), "type", 200, 5, 100);

        System.out.println("Database configuration-------------");
        System.out.println("name: " + database.getDatabase() );
        System.out.println("host: " + database.getHostname() );
        System.out.println("jdbc url: " + database.getJdbcUrl() );
        System.out.println("port: " + database.getPort() );
        System.out.println("user name: " + database.getUsername() );
        System.out.println("-----------------------------------");
    }

    public void dropCreate(DatabaseClient client) {

        List<String> statements = Arrays.asList(//
                "DROP TABLE IF EXISTS travel_time_entity;",
                "CREATE TABLE TRAVEL_TIME_ENTITY\n" +
                        "(\n" +
                        "    pkey SERIAL PRIMARY KEY,\n" +
                        "    id                         VARCHAR(200),\n" +
                        "    name                       VARCHAR(200),\n" +
                        "    pub_date                   TIMESTAMP WITH TIME ZONE  NOT NULL,\n" +
                        "    retrieved_from_third_party TIMESTAMP WITH TIME ZONE  NOT NULL,\n" +
                        "    type                       VARCHAR(50),\n" +
                        "    length                     SMALLINT CHECK (length >= -1),\n" +
                        "    velocity                   SMALLINT CHECK (velocity >= -1),\n" +
                        "    travel_time                SMALLINT CHECK (travel_time >= -1),\n" +
                        "    unique (id, pub_date)\n" +
                        ");");

        statements.forEach(it -> client.execute(it) //
                .fetch() //
                .rowsUpdated() //
                .as(StepVerifier::create) //
                .expectNext(0)
                .verifyComplete());
    }

    @Test
    public void insertEntityOne() {
        dropCreate(client);

        client.insert()
                .into(TravelTimeEntity.class)
                .using(entityOne)
                .fetch()
                .rowsUpdated()
                .as(StepVerifier::create)
                .expectNext(1)
                .verifyComplete();
    }

    @Test
    public void isUnique() {

        dropCreate(client);

        String pubDate = "2019-10-16T15:52:00Z";
        String retDate = "2019-10-16T16:00:00Z";

        entityOne = new TravelTimeEntity(null, "002", "First entity", OffsetDateTime.parse(pubDate), OffsetDateTime.parse(retDate), "type", 200, 5, 100);
        entitySameAsOne = new TravelTimeEntity(null, "002", "First entity", OffsetDateTime.parse(pubDate), OffsetDateTime.parse(retDate), "type", 200, 5, 100);

        client.insert()
                .into(TravelTimeEntity.class)
                .using(entityOne)
                .fetch()
                .rowsUpdated()
                .as(StepVerifier::create)
                .expectNext(1)
                .verifyComplete();

        Boolean isUnique = Optional.ofNullable(repo.isUnique(entitySameAsOne).block())
                .orElseThrow(NoSuchElementException::new);
        Assert.assertFalse(isUnique);

        String newPubDate = "2019-10-16T15:53:00Z";
        entitySameAsOneWithNewPubDate = new TravelTimeEntity(null, "002", "First entity", OffsetDateTime.parse(newPubDate), OffsetDateTime.parse(retDate), "type", 200, 5, 100);

        isUnique = Optional.ofNullable(repo.isUnique(entitySameAsOneWithNewPubDate).block())
                .orElseThrow(NoSuchElementException::new);

        Assert.assertTrue(isUnique);
    }

    @Test
    public void save() {

        dropCreate(client);
        repo.save(entityOne).subscribe();

       FetchSpec<TravelTimeEntity> entity = client.execute("SELECT pkey, id, name, pub_date, retrieved_from_third_party, type, length, velocity, travel_time FROM travel_time_entity;")
                        .as(TravelTimeEntity.class)
                        .fetch();
       assertThat(entity.rowsUpdated()).isNotEqualTo(0);

        FetchSpec<TravelTimeEntity> entity2 = client.execute("SELECT pkey, id, name, pub_date, retrieved_from_third_party, type, length, velocity, travel_time FROM travel_time_entity;")
                .as(TravelTimeEntity.class)
                .fetch();

        TravelTimeEntity retrieved = entity2.first().block();

        Assert.assertSame(retrieved.getPkey(), 1);
        Assert.assertEquals(retrieved.getId(), "002");
        Assert.assertEquals(retrieved.getName(), "First entity");
        Assert.assertTrue(retrieved.getPubDate().isEqual(OffsetDateTime.parse("2019-10-16T15:52:00Z")));
        Assert.assertTrue(retrieved.getRetrievedFromThirdParty().isEqual(OffsetDateTime.parse("2019-10-16T16:00:00Z")));
        Assert.assertEquals(retrieved.getType(), "type");
        Assert.assertTrue(retrieved.getLength() == 200);
        Assert.assertTrue(retrieved.getTravel_time() == 5);
        Assert.assertTrue(retrieved.getVelocity() == 100);
    }

    @Test
    public void getLastStored() {
        dropCreate(client);
        insertEntityOne();
        repo.getLastStored(entityOne)
                .as(StepVerifier::create)
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    public void didPropertiesChange() {
        dropCreate(client);
        insertEntityOne();

        repo.didPropertiesChange(entitySameAsOne)
                .as(StepVerifier::create)
                .expectNext(false)
                .verifyComplete();

        repo.didPropertiesChange(entitySameAsOneChangedProperties)
                .as(StepVerifier::create)
                .expectNext(true)
                .verifyComplete();
    }
}
