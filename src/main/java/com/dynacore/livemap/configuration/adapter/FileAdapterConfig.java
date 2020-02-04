package com.dynacore.livemap.configuration.adapter;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@ConfigurationProperties(prefix = "fileadapter")
@Component
public class FileAdapterConfig {

  private String folder;
}
