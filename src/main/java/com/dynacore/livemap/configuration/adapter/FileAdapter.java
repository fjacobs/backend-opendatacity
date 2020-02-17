package com.dynacore.livemap.configuration.adapter;

import com.dynacore.livemap.core.adapter.GeoJsonAdapter;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.geojson.FeatureCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.FileSystemResource;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.io.File;
import java.util.Arrays;
import java.util.stream.Collectors;

@Configuration
public class FileAdapter {

  private final Logger logger = LoggerFactory.getLogger(FileAdapter.class);
  private final FileAdapterConfig config;

  public FileAdapter(FileAdapterConfig config) {
    this.config = config;
  }

  Mono<FeatureCollection> readFile(File file) {
    Mono<FeatureCollection> blockingWrapper =
        Mono.fromCallable(
                () -> {
                  FeatureCollection fc =
                      new ObjectMapper()
                          .configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true)
                          .readValue(file, FeatureCollection.class);
                  logger.info("Importing file: " + file.getName());
                  return fc;
                })
            .cast(FeatureCollection.class);

    return blockingWrapper.subscribeOn(Schedulers.boundedElastic());
  }

  @Profile("file")
  @Bean(name = "fileReader")
  GeoJsonAdapter fileReader() {
    logger.info("Bean: fileReader ");
    logger.info("Reading file contents from folder: " + config.getFolder());

    FileSystemResource resource =
        new FileSystemResource(System.getProperty("user.home") + "/" + config.getFolder());
    File folder = resource.getFile();
    File[] files = folder.listFiles();

    if (files == null) {
      System.err.println("Error: Could not open folder: " + config.getFolder());
      return interval -> Flux.empty();
    }

    Arrays.sort(files);
    var monoList = Arrays.stream(files).filter(File::isFile).collect(Collectors.toList());

    return interval -> {
        return Flux.fromIterable(monoList)
                .delayElements(interval)
                .flatMap(this::readFile)
                .onBackpressureDrop(
                        fc ->
                                logger.error(
                                        "Drop on backpressure:"
                                                + fc.getFeatures().get(0).getProperty("Timestamp")));
    };
  }
}
