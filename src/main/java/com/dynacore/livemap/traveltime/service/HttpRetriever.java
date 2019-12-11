package com.dynacore.livemap.traveltime.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.geojson.FeatureCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Optional;

import static org.springframework.http.HttpStatus.NOT_MODIFIED;

@Profile("prod")
@Component
public class HttpRetriever implements OpenDataRetriever {

    @Autowired
    WebClient webClient;

    public Flux<FeatureCollection> requestFeatures() {
        return Flux.interval(Duration.ofSeconds(4))
                   .concatMap(tick-> webClient
                   .get()
                   .exchange()
                   .filter(clientResponse -> (clientResponse.statusCode() != NOT_MODIFIED))
                   .flatMap(clientResponse -> clientResponse.bodyToMono(byte[].class))
                   .map(bytes -> {
                        FeatureCollection featureColl = null;
                        try {
                            featureColl = Optional.of(new ObjectMapper().readValue(bytes, FeatureCollection.class))
                                .orElseThrow(IllegalStateException::new);
                            } catch (Exception e) {
                                return Mono.error(new IllegalArgumentException("Could not serialize GeoJson."));
                            }
                                return featureColl;
                            }
                        )
                   .cast(FeatureCollection.class));
    }
}
