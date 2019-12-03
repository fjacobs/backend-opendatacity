package com.dynacore.livemap.testing;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.data.domain.Sort.Order.*;
import static org.springframework.data.r2dbc.query.Criteria.*;

import io.r2dbc.spi.ConnectionFactory;
import lombok.Data;
import org.junit.Assert;
import org.springframework.data.r2dbc.core.DatabaseClient;
import reactor.test.StepVerifier;

import javax.sql.DataSource;

import org.assertj.core.api.Condition;
import org.junit.Before;
import org.junit.Test;

import org.springframework.dao.DataAccessException;

import org.springframework.jdbc.core.JdbcTemplate;

/**
 * Integration tests for {@link DatabaseClient}.
 *
 * Original:
 * @author Mark Paluch
 */
public abstract class AbstractDatabaseClientIntegrationTests extends R2dbcIntegrationTestSupport {

    private ConnectionFactory connectionFactory;

    private JdbcTemplate jdbc;

//    @Before
//    public void before() {
//
//        connectionFactory = createConnectionFactory();
//
//        jdbc = createJdbcTemplate(createDataSource());
//
//        try {
//            jdbc.execute("DROP TABLE legoset");
//        } catch (DataAccessException e) {
//        }
//        jdbc.execute(getCreateTableStatement());
//    }

    /**
     * Creates a {@link DataSource} to be used in this test.
     *
     * @return the {@link DataSource} to be used in this test.
     */
    protected abstract DataSource createDataSource();

    /**
     * Creates a {@link ConnectionFactory} to be used in this test.
     *
     * @return the {@link ConnectionFactory} to be used in this test.
     */
    protected abstract ConnectionFactory createConnectionFactory();

    /**
     * Returns the the CREATE TABLE statement for table {@code legoset} with the following three columns:
     * <ul>
     * <li>id integer (primary key), not null</li>
     * <li>name varchar(255), nullable</li>
     * <li>manual integer, nullable</li>
     * </ul>
     *
     * @return the CREATE TABLE statement for table {@code legoset} with three columns.
     */
}