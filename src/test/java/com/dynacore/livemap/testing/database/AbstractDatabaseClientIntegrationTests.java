package com.dynacore.livemap.testing.database;

import io.r2dbc.spi.ConnectionFactory;
import org.springframework.data.r2dbc.core.DatabaseClient;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;

/**
 * Integration tests for {@link DatabaseClient}.
 *
 * <p>Original:
 *
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
   * Returns the the CREATE TABLE statement for table {@code legoset} with the following three
   * columns:
   *
   * <ul>
   *   <li>id integer (primary key), not null
   *   <li>name varchar(255), nullable
   *   <li>manual integer, nullable
   * </ul>
   *
   * @return the CREATE TABLE statement for table {@code legoset} with three columns.
   */
}
