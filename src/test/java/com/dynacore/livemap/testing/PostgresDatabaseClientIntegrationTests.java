package com.dynacore.livemap.testing;

import io.r2dbc.spi.ConnectionFactory;

import javax.sql.DataSource;

import org.junit.ClassRule;

import org.postgresql.ds.PGSimpleDataSource;
import org.springframework.data.r2dbc.core.DatabaseClient;


/**
 * Integration tests for {@link DatabaseClient} against PostgreSQL.
 *
 * @author Mark Paluch
 */
public class PostgresDatabaseClientIntegrationTests extends AbstractDatabaseClientIntegrationTests {

    @ClassRule public static final ExternalDatabase database = PostgresTestSupport.database();

    @Override
    protected DataSource createDataSource() {
        return PostgresTestSupport.createDataSource(database);
    }

    @Override
    protected ConnectionFactory createConnectionFactory() {
        return PostgresTestSupport.createConnectionFactory(database);
    }
    /**
     * Creates a new {@link DataSource} configured from the {@link ExternalDatabase}.
     */
    public static DataSource createDataSource(ExternalDatabase database) {

        PGSimpleDataSource dataSource = new PGSimpleDataSource();

        dataSource.setUser(database.getUsername());
        dataSource.setPassword(database.getPassword());
		dataSource.setURL(database.getJdbcUrl());
       // dataSource.setURL("jdbc:postgresql://localhost:5432/trafficdata");
        return dataSource;
    }
}
