package com.dynacore.livemap;

import io.r2dbc.spi.ConnectionFactories;
import io.r2dbc.spi.ConnectionFactory;
import io.r2dbc.spi.ConnectionFactoryOptions;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import static io.r2dbc.spi.ConnectionFactoryOptions.*;

@SpringBootApplication
@EnableTransactionManagement
public class DatabaseConfig {

}
