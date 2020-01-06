package com.dynacore.livemap.configuration;


import io.r2dbc.spi.ConnectionFactories;
import io.r2dbc.spi.ConnectionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.r2dbc.config.AbstractR2dbcConfiguration;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;

import static io.r2dbc.spi.ConnectionFactoryOptions.*;

@Profile("postgres")
@Configuration
@EnableR2dbcRepositories
public class PostgresConfig extends AbstractR2dbcConfiguration {

    @Bean("postgresPoolConnFactory")
    public ConnectionFactory connectionFactory() {

        return ConnectionFactories.get(builder()
                .option(DRIVER, "pool")
                .option(PROTOCOL, "postgresql")
                .option(HOST, "localhost")
                .option(PORT, 5432)
                .option(USER, "postgres")
                .option(PASSWORD, "admin")
                .option(DATABASE, "trafficdata")
                .build());
    }
}
