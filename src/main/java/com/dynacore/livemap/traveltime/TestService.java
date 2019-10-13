package com.dynacore.livemap.traveltime;

import com.dynacore.livemap.common.http.HttpClientFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.geojson.Feature;
import org.geojson.FeatureCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.netty.http.client.HttpClient;
import reactor.util.function.Tuple2;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.stream.Stream;

import static reactor.core.publisher.Flux.generate;

@Profile("testing")
@Service
public class TestService {
    private static final String SOURCEURL = "http://web.redant.net/~amsterdam/ndw/data/reistijdenAmsterdam.geojson";
    private final Logger logger = LoggerFactory.getLogger(TestService.class);
    HttpClientFactory httpClientFactory;
    WebClient webClient;

    TestService(HttpClientFactory httpClientFactory) {
        this.httpClientFactory = httpClientFactory;
        HttpClient httpClient = httpClientFactory.autoConfigHttpClient(SOURCEURL);
        webClient = WebClient.builder().clientConnector(new ReactorClientHttpConnector(httpClient)).build();

        try {
            init();
        } catch (Exception e) {
            System.err.println(e);
        }
    }

    private void init() {
        pollServer().subscribe(System.out::println);
    }

     Flux<Flux<Feature>> pollServer() {
        return Flux.<Flux<Feature>>generate(sink -> {
                    sink.next(dataSourceSubscription());
                })
                .delayElements(Duration.ofSeconds(5))
                .log();
    }

    Flux<Feature> dataSourceSubscription() {
        org.geojson.FeatureCollection mono = webClient.get()
                .uri(SOURCEURL)
                .retrieve()
                .bodyToMono(byte[].class) // D
                .delayElement(Duration.ofSeconds(3))
                // Data source doesn't include a content-type header: convert from bytes to json
                .map(bytes -> {
                    org.geojson.FeatureCollection fc = null;
                    try {
                        if(bytes != null) {
                            fc = Optional.of(new ObjectMapper().readValue(bytes, org.geojson.FeatureCollection.class))
                                    .orElseThrow();
                        } else {
                            fc = new FeatureCollection();
                        }
                    } catch (IOException e) {
                        logger.error("Error: " + e);
                        fc = new FeatureCollection();
                    }
                    return fc;
                })
                .doOnSuccess(x -> logger.info("Serialized Mono<FeatureCollection> from datasource"))
                .retryBackoff(500,Duration.ofSeconds(2))
                .block();
        return Flux.fromIterable(mono.getFeatures());
        //   .take(10);

    }
}