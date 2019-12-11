package com.dynacore.livemap.testing.database;


import io.r2dbc.spi.ConnectionFactory;

import java.util.function.Supplier;
import java.util.stream.Stream;

import com.dynacore.livemap.testing.database.ExternalDatabase.ProvidedDatabase;
import org.testcontainers.containers.PostgreSQLContainer;
import org.postgresql.ds.PGSimpleDataSource;

import javax.sql.DataSource;

/**
 * Utility class for testing against Postgres.
 *
 * This file is copied from the org.springframework.data.r2dbc project, it's original authors are:
 *
 * @author Mark Paluch
 * @author Jens Schauder
 */
public class PostgresTestSupport {

    private static final boolean PREFER_LOCAL = true;
    private static ExternalDatabase testContainerDatabase;

    public static final String CREATE_TABLE_TRAVEL_TIME =  "CREATE TABLE travel_time_entity\n" +
            "(\n" +
            "    pkey SERIAL PRIMARY KEY,\n" +
            "    id                         VARCHAR(200),\n" +
            "    name                       VARCHAR(200),\n" +
            "    pub_date                   TIMESTAMP WITH TIME ZONE  NOT NULL,\n" +
            "    retrieved_from_third_party TIMESTAMP WITH TIME ZONE  NOT NULL,\n" +
            "    type                       VARCHAR(200),\n" +
            "    length                     SMALLINT CHECK (length >= -1),\n" +
            "    velocity                   SMALLINT CHECK (velocity >= -1),\n" +
            "    travel_time                SMALLINT CHECK (travel_time >= -1),\n" +
            "    unique (id, pub_date)\n" +
            ");";

    /**
     * Returns a database either hosted locally at {@code postgres:@localhost:5432/postgres} or running inside Docker.
     *
     * @return information about the database. Guaranteed to be not {@literal null}.
     */
    public static ExternalDatabase database() {

        if (PREFER_LOCAL) {
            return getFirstWorkingDatabase( //
                    PostgresTestSupport::local, //
                    PostgresTestSupport::testContainer //
            );
        } else {
            return getFirstWorkingDatabase( //
                    PostgresTestSupport::testContainer, //
                    PostgresTestSupport::local //
            );
        }
    }

    @SafeVarargs
    private static ExternalDatabase getFirstWorkingDatabase(Supplier<ExternalDatabase>... suppliers) {

        return Stream.of(suppliers).map(Supplier::get) //
                .filter(ExternalDatabase::checkValidity) //
                .findFirst() //
                .orElse(ExternalDatabase.unavailable());
    }

    /**
     * Returns a locally provided database at {@code postgres:@localhost:5432/postgres}.
     */
    private static ExternalDatabase local() {

        return ProvidedDatabase.builder() //
                .hostname("localhost") //
                .port(5432) //
                .database("test") //
                .username("postgres") //
                .password("admin").build();

    }

    /**
     * Returns a database provided via Testcontainers.
     */
    private static ExternalDatabase testContainer() {

        if (testContainerDatabase == null) {
            try {
                PostgreSQLContainer container = new PostgreSQLContainer();
                container.start();

                testContainerDatabase = ProvidedDatabase.from(container);

            } catch (IllegalStateException ise) {
                // docker not available.
                testContainerDatabase = ExternalDatabase.unavailable();
            }
        }
        return testContainerDatabase;
    }

    /**
     * Creates a new {@link ConnectionFactory} configured from the {@link ExternalDatabase}..
     */
    public static ConnectionFactory createConnectionFactory(ExternalDatabase database) {
        return ConnectionUtils.getConnectionFactory("postgresql", database);
    }

    /**
     * Creates a new {@link DataSource} configured from the {@link ExternalDatabase}.
     */
    public static DataSource createDataSource(ExternalDatabase database) {

        PGSimpleDataSource dataSource = new PGSimpleDataSource();

        dataSource.setUser(database.getUsername());
        dataSource.setPassword(database.getPassword());
		//dataSource.setURL(database.getJdbcUrl());
        dataSource.setURL("jdbc:postgresql://localhost:5432/trafficdata");

        return dataSource;
    }

}
