package com.dynacore.livemap.testing.database;

import io.r2dbc.h2.H2ConnectionConfiguration;
import io.r2dbc.h2.H2ConnectionFactory;
import io.r2dbc.spi.ConnectionFactory;

public class H2TestSupport {

    public static final String CREATE_TABLE_TRAVEL_TIME =  "CREATE TABLE travel_time_entity\n" +
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
            ");";

    public static ConnectionFactory createConnectionFactory() {
        return new H2ConnectionFactory(H2ConnectionConfiguration.builder() //
                .file("~/r2dbc") //
                .username("sa") //
                .password("") //
                .option("DB_CLOSE_DELAY=-1").build());
    }
}
