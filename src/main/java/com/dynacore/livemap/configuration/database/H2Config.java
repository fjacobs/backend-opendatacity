package com.dynacore.livemap.configuration.database;

import io.r2dbc.spi.ConnectionFactories;
import io.r2dbc.spi.ConnectionFactory;
import io.r2dbc.spi.ConnectionFactoryOptions;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.r2dbc.config.AbstractR2dbcConfiguration;
import org.springframework.data.r2dbc.connectionfactory.init.CompositeDatabasePopulator;
import org.springframework.data.r2dbc.connectionfactory.init.ConnectionFactoryInitializer;
import org.springframework.data.r2dbc.connectionfactory.init.ResourceDatabasePopulator;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;

@Profile("h2")
@Configuration
@EnableR2dbcRepositories(basePackages = "com.dynacore.livemap.traveltime.repo")
public class H2Config extends AbstractR2dbcConfiguration {

  private boolean enablePopulator = true;

  public void enablePopulator(boolean enablePopulator) {
    this.enablePopulator = enablePopulator;
  }

  @Bean("h2ConnectionFactory")
  public ConnectionFactory connectionFactory() {
    return ConnectionFactories.get(
        ConnectionFactoryOptions.parse(
            "r2dbc:h2:mem:////trafficdata;DB_CLOSE_DELAY=-1?MODE=PostgreSQL"));
  }

  @Bean
  public ConnectionFactoryInitializer prodInitializer(
      @Qualifier("h2ConnectionFactory") ConnectionFactory connectionFactory) {

    ConnectionFactoryInitializer initializer = new ConnectionFactoryInitializer();
    initializer.setConnectionFactory(connectionFactory);

    CompositeDatabasePopulator populator = new CompositeDatabasePopulator();
    populator.addPopulators(new ResourceDatabasePopulator(new ClassPathResource("schema.sql")));
    initializer.setDatabasePopulator(populator);
    initializer.setEnabled(enablePopulator);

    return initializer;
  }
}
