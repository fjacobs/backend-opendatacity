package com.dynacore.livemap.configuration.adapter;

import com.dynacore.livemap.core.adapter.GeoJsonAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

@Configuration
public class FileAdapter {

  private final Logger logger = LoggerFactory.getLogger(FileAdapter.class);
  private FileAdapterConfig config;

  public FileAdapter(FileAdapterConfig config) {
    this.config = config;
  }

  @Profile("file")
  @Bean(name = "fileReaderRepeat")
  GeoJsonAdapter fileReaderRepeat() {
    logger.info("Bean: fileReaderRepeat ");
    logger.info("Reading file contents from folder: " +  config.getFolder());

    return (interval) ->
        Flux.fromIterable(FileToGeojson.readCollection(config.getFolder()))
            .publishOn(Schedulers.boundedElastic())
            .delayElements(interval)
            .repeat();
  }
}
