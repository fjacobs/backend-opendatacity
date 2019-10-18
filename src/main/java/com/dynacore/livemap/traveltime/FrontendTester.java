package com.dynacore.livemap.traveltime;

import com.dynacore.livemap.common.http.HttpClientFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.geojson.Feature;
import org.geojson.FeatureCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.ConnectableFlux;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.util.Optional;

import static org.springframework.http.HttpStatus.NOT_MODIFIED;

//@Primary
public class FrontendTester implements TravelTimeService {

    private ConnectableFlux<Feature> sharedFlux;
    private WebClient webClient;

    @Autowired
    public FrontendTester(TravelTimeRepo repo, HttpClientFactory httpClientFactory) {
        ReactorClientHttpConnector httpConnector = new ReactorClientHttpConnector(httpClientFactory.autoConfigHttpClient(SOURCEURL));
        webClient = WebClient.builder().clientConnector(httpConnector)
                .build();

        sharedFlux = requestFeatures().filterWhen(x -> repo.didPropertiesChange(convertToEntity(x)))
                .share()
                .cache(Duration.ofSeconds(INTERVAL))
                .publish();

        var saveFlux = Flux.from(sharedFlux)
                .parallel(Runtime.getRuntime().availableProcessors())
                .runOn(Schedulers.parallel())
                .map(this::convertToEntity)
                .doOnNext(repo::save)
                .sequential();


        Flux.interval(Duration.ofSeconds(INTERVAL))
                .map(tick -> {
                    saveFlux.subscribe();
                    sharedFlux.connect();
                    logger.info("Interval count: " + tick);
                    return tick;
                }).subscribe();
    }

    private Flux<Feature> requestFeatures() {
        return webClient.get()
                .uri(SOURCEURL)
                .exchange()
                .doOnNext(clientResponse -> logger.info("Server responded: " + clientResponse.statusCode().toString()))
                .filter(clientResponse -> (clientResponse.statusCode() != NOT_MODIFIED))
                .flatMap(clientResponse -> clientResponse.bodyToMono(byte[].class))
                .map(bytes -> {
                            FeatureCollection featureColl = null;
                            try {
                                featureColl = Optional.of(new ObjectMapper().readValue(bytes, FeatureCollection.class))
                                        .orElseThrow(IllegalStateException::new);
                            } catch (Exception e) {
                                return Mono.error(IllegalStateException::new);
                            }
                            return featureColl;
                        }
                )
                .cast(FeatureCollection.class)
                .doOnNext(req -> logger.info("Serialized " + req.getFeatures().size() + " of features"))
                .flatMapMany(this::processFeatures);
    }

}
