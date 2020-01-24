package com.dynacore.livemap.configuration.database;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotBlank;

@Getter @Setter
@ConfigurationProperties(prefix = "postgres")
@Component
public class PostgresConfiguration {

    @NotBlank String host;
    @NotBlank int port;
    @NotBlank String user;
    @NotBlank String password;
    @NotBlank String database;
}
