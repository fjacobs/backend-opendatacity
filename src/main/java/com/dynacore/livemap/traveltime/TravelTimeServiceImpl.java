package com.dynacore.livemap.traveltime;

import com.dynacore.livemap.common.http.HttpClientFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.geojson.Feature;
import org.geojson.FeatureCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.ConnectableFlux;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.util.Optional;

import static org.springframework.http.HttpStatus.NOT_MODIFIED;

@Profile("traveltime")
@Service("travelTimeService")
class TravelTimeServiceImpl implements TravelTimeService {

    private final Logger logger = LoggerFactory.getLogger(TravelTimeServiceImpl.class);
    private ConnectableFlux<Feature> sharedFlux;
    private WebClient webClient;

    @Autowired
    TravelTimeServiceImpl(TravelTimeRepo repo, HttpClientFactory httpClientFactory) {

//        ReactorClientHttpConnector httpConnector = new ReactorClientHttpConnector(httpClientFactory.autoConfigHttpClient(SOURCEURL));
//        webClient = WebClient.builder().clientConnector(httpConnector)
//                .build();

        sharedFlux = requestFeaturesFile()
                 // .filterWhen(v -> repo.didPropertiesChange(convertToEntity(v)))
                .share()
                .cache(Duration.ofSeconds(INTERVAL))
                .publish();

        var saveFlux = Flux.from(sharedFlux)
                .parallel(Runtime.getRuntime().availableProcessors())
                .runOn(Schedulers.parallel())
                .map(this::convertToEntity)
                .sequential();
        //       .doOnNext(repo::save);

        Flux.interval(Duration.ofSeconds(INTERVAL))
                .map(tick -> {
                    saveFlux.subscribe();
                    sharedFlux.connect();
                    logger.info("Interval count: " + tick);
                    return tick;
                }).subscribe();
    }

    Flux<Feature> getPublisher() {
        return Flux.from(sharedFlux);
    }

    Flux<Feature> requestFeaturesHttp() {
        return httpRequest()
                .doOnNext(req -> logger.info("Serialized " + req.getFeatures().size() + " of features"))
                .flatMapMany(this::processFeatures);
    }

    Mono<FeatureCollection> httpRequest() {
        return webClient.get()
                .uri(SOURCEURL)
                .exchange()
                .doOnNext(response -> logger.info("Server responded: " + response.statusCode().toString()))
                .filter(response -> (response.statusCode() != NOT_MODIFIED))
                .flatMap(response -> response.bodyToMono(byte[].class))
                .map(bytes -> {
                            FeatureCollection featureColl = null;
                            try {
                                featureColl = Optional.of( new ObjectMapper().readValue(bytes, FeatureCollection.class) )
                                                      .orElseThrow(IllegalStateException::new);
                            } catch (Exception e) {
                                return Mono.error(IllegalStateException::new);
                            }
                            return featureColl;
                        }
                )
                .cast(FeatureCollection.class);
    }
    // Test methods
    Flux<Feature> requestFeaturesFile() {
        return new FrontendTester().fileFeatureCollection()
                .flatMapMany(this::processFeatures);

    }
}
