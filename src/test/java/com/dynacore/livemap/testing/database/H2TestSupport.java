package com.dynacore.livemap.testing.database;

import io.r2dbc.h2.H2ConnectionConfiguration;
import io.r2dbc.h2.H2ConnectionFactory;
import io.r2dbc.spi.ConnectionFactory;

public class H2TestSupport {

    public static ConnectionFactory createConnectionFactory() {
        return new H2ConnectionFactory(H2ConnectionConfiguration.builder() //
                .inMemory("r2dbc") //
                .username("sa") //
                .password("") //
                .option("DB_CLOSE_DELAY=-1").build());
    }
}