package com.dynacore.livemap.configuration;


import io.r2dbc.spi.ConnectionFactories;
import io.r2dbc.spi.ConnectionFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.r2dbc.config.AbstractR2dbcConfiguration;
import org.springframework.data.r2dbc.connectionfactory.init.CompositeDatabasePopulator;
import org.springframework.data.r2dbc.connectionfactory.init.ConnectionFactoryInitializer;
import org.springframework.data.r2dbc.connectionfactory.init.ResourceDatabasePopulator;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;

import static io.r2dbc.spi.ConnectionFactoryOptions.*;

@Profile("prod")
@Configuration
@EnableR2dbcRepositories
public class PostgresConfig extends AbstractR2dbcConfiguration {

    private boolean enablePopulator = false;

    public void enablePopulator(boolean enablePopulator){
        this.enablePopulator=enablePopulator;
    }

    @Bean("postgresPoolConnFactory")
    public ConnectionFactory connectionFactory() {

        return ConnectionFactories.get(builder()
                .option(DRIVER, "pool")
                .option(PROTOCOL, "postgresql") // driver identifier, PROTOCOL is delegated as DRIVER by the pool.
                .option(HOST, "localhost")
                .option(PORT, 5432)
                .option(USER, "postgres")
                .option(PASSWORD, "admin")
                .option(DATABASE, "trafficdata")
                .build());
    }

    @Bean
    public ConnectionFactoryInitializer prodInitializer(@Qualifier("postgresPoolConnFactory") ConnectionFactory connectionFactory) {

        ConnectionFactoryInitializer initializer = new ConnectionFactoryInitializer();
        initializer.setConnectionFactory(connectionFactory);

        CompositeDatabasePopulator populator = new CompositeDatabasePopulator();
        populator.addPopulators(new ResourceDatabasePopulator(new ClassPathResource("schema.sql")));
        initializer.setDatabasePopulator(populator);
        initializer.setEnabled(enablePopulator);

        return initializer;
    }
}
