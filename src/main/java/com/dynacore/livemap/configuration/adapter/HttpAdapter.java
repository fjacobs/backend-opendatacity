package com.dynacore.livemap.configuration.adapter;

import org.geojson.FeatureCollection;
import com.dynacore.livemap.core.adapter.GeoJsonAdapter;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Optional;

import static org.springframework.http.HttpStatus.NOT_MODIFIED;

@Profile("http")
@Component
public class HttpAdapter implements GeoJsonAdapter {

  private WebClient webClient;

  public HttpAdapter(WebClient webClient) {
    this.webClient = webClient;
  }

  public Flux<FeatureCollection> adapterHotSourceReq(Duration interval) {
    return Flux.interval(interval)
        .concatMap(
            tick ->
                webClient
                    .get()
                    .exchange()
                    .filter(clientResponse -> (clientResponse.statusCode() != NOT_MODIFIED))
                    .flatMap(clientResponse -> clientResponse.bodyToMono(byte[].class))
                    .map(
                        bytes -> {
                          FeatureCollection featureColl = null;
                          try {
                            featureColl =
                                Optional.of(
                                        new ObjectMapper()
                                            .readValue(bytes, FeatureCollection.class))
                                    .orElseThrow(IllegalStateException::new);
                          } catch (Exception e) {
                            return Mono.error(
                                new IllegalArgumentException("Could not serialize GeoJson."));
                          }
                          return featureColl;
                        })
                    .cast(FeatureCollection.class));
  }
}
