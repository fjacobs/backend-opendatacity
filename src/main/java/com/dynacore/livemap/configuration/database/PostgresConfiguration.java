package com.dynacore.livemap.configuration.database;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotBlank;

@Profile("postgres")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ConfigurationProperties(prefix = "postgres")
@Component
public class PostgresConfiguration {

  @NotBlank String host;
  @NotBlank int port;
  @NotBlank String user;
  @NotBlank String password;
  @NotBlank String database;
}
