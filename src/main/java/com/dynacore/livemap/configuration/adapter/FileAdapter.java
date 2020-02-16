package com.dynacore.livemap.configuration.adapter;

import com.dynacore.livemap.core.adapter.GeoJsonAdapter;
import com.fasterxml.jackson.core.JsonProcessingException;
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
import java.time.Duration;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

@Configuration
public class FileAdapter {

  private final Logger logger = LoggerFactory.getLogger(FileAdapter.class);
  private FileAdapterConfig config;

  public FileAdapter(FileAdapterConfig config) {
    this.config = config;
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
    Arrays.sort(Objects.requireNonNull(files));
    var monoList = Arrays.stream(files).filter(File::isFile).collect(Collectors.toList());

    return new GeoJsonAdapter() {
        @Override
        public Flux<FeatureCollection> adapterHotSourceReq(Duration interval) throws JsonProcessingException {
            return Flux.fromIterable(monoList)
                    .map(
                            file -> {
                                Mono<FeatureCollection> blockingWrapper =
                                        Mono.fromCallable(
                                                () -> {
                                                    FeatureCollection fc =
                                                            new ObjectMapper()
                                                                    .configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true)
                                                                    .readValue(file, FeatureCollection.class);
                                                    return fc;
                                                });
                                blockingWrapper = blockingWrapper.subscribeOn(Schedulers.boundedElastic());

                                return blockingWrapper;
                            })
                    .flatMap(featureCollectionMono -> featureCollectionMono);
        }
    };
  }
}
