package com.dynacore.livemap;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.geojson.FeatureCollection;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.FileSystemResource;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.io.File;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

public class FileReadTest {

  @Test
  public void readFile() {
    FileSystemResource resource =
        new FileSystemResource(
            System.getProperty("user.home")
                + "/guidancesign/2020-02-12_19-23-59.GuidanceSign.json");
    File file = resource.getFile();

    System.out.println(file.exists());
  }

  // See https://projectreactor.io/docs/core/release/reference/#faq.wrap-blocking
  @Test
  public void properWrappingOfSynchronousBlockingCall() {

    FileSystemResource resource =
        new FileSystemResource(System.getProperty("user.home") + "/guidancesign");

    File folder = resource.getFile();
    File[] files = folder.listFiles();
    Arrays.sort(Objects.requireNonNull(files));
    var monoList = Arrays.stream(files).filter(File::isFile).collect(Collectors.toList());

    Flux.fromIterable(monoList)
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
        .flatMap(featureCollectionMono -> featureCollectionMono)
        .blockLast();
  };
}
