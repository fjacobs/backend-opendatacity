package com.dynacore.livemap.traveltime.service;

import com.dynacore.livemap.traveltime.repo.TravelTimeEntity;

import com.dynacore.livemap.traveltime.repo.TravelTimeRepo;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.geojson.Feature;
import org.geojson.FeatureCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.ConnectableFlux;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.Clock;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.Optional;

import static org.springframework.http.HttpStatus.NOT_MODIFIED;

/*
    This service will periodically retrieve and store road traffic information from a public open data service.
    Additionally, a reactive subscription is available for the GeoJson.
 */

@Profile("traveltime")
@Service("travelTimeService")
public class TravelTimeService {

    private static final String ID = "Id";
    private static final String NAME = "Name";
    private static final String TYPE = "Type";
    private static final String TRAVEL_TIME = "Traveltime";
    private static final String LENGTH = "Length";
    private static final String VELOCITY = "Velocity";
    private static final String OUR_RETRIEVAL = "retrievedFromThirdParty";
    private static final String THEIR_RETRIEVAL = "Timestamp";
    private static final String DYNACORE_ERRORS = "dynacoreErrors";

    //-------

    private final Logger logger = LoggerFactory.getLogger(TravelTimeService.class);
    private ConnectableFlux<Feature> sharedFlux;
    private final WebClient webClient;
    private final TravelTimeRepo repo;
    private final ServiceConfig config;

    @Autowired
    public TravelTimeService(TravelTimeRepo repo, WebClient webClient, ServiceConfig config) {

        this.webClient = webClient;
        this.repo = repo;
        this.config = config;
        pollProcessor();
    }

    private void pollProcessor() {

        sharedFlux = httpRequestFeatures().share()
                .cache(Duration.ofSeconds(config.getRequestInterval()))
                .publish();

        var saveFlux = Flux.from(sharedFlux)
                .parallel(Runtime.getRuntime().availableProcessors())
                .runOn(Schedulers.parallel())
                .map(feature-> {
                    repo.save(convertToEntity(feature));
                    return feature;
                })
                .sequential();

        Flux.interval(Duration.ofSeconds(config.getRequestInterval()))
                .map(tick -> {
                    saveFlux.subscribe();
                    sharedFlux.connect();
                    logger.info("Interval count: " + tick);
                    return tick;
                }).subscribe();
    }

    public Flux<Feature> getFeatures() {
        return Flux.from(sharedFlux);
    }

    public Mono<FeatureCollection> getFeatureCollection(Flux<Feature> featureFlux) {

        return featureFlux.collectList()
                .map(features -> {
                    FeatureCollection fc = new FeatureCollection();
                    fc.setFeatures(features);
                    return fc;
                });
    }

    private TravelTimeEntity convertToEntity(Feature travelTime) {

        return new TravelTimeEntity.Builder()
                .id((String) travelTime.getProperties().get(ID))
                .name((String) travelTime.getProperties().get(NAME))
                .pubDate((String) travelTime.getProperties().get(THEIR_RETRIEVAL))
                .retrievedFromThirdParty((String) travelTime.getProperties().get(OUR_RETRIEVAL))
                .type((String) travelTime.getProperties().get(TYPE))
                .travelTime((int) travelTime.getProperties().get(TRAVEL_TIME))
                .velocity((int) travelTime.getProperties().get(VELOCITY))
                .length((int) travelTime.getProperties().get(LENGTH)).build();
    }

    private Flux<Feature> httpRequestFeatures() {

        return webClient
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
                .cast(FeatureCollection.class)
                .flatMapMany(this::processFeatures);
    }

    private Flux<Feature> processFeatures(FeatureCollection fc) {

        return Flux.fromIterable(fc)
                .parallel(Runtime.getRuntime().availableProcessors())
                .runOn(Schedulers.parallel())
                .map(this::processFeature)
                .sequential()
                .share()
                .doOnSubscribe(s -> logger.info("Subscribed to processed features source"))
                .doOnComplete(() -> logger.info("Completed processing features"));
    }

    private Feature processFeature(Feature feature) {

         try {
              OffsetDateTime retrieved = OffsetDateTime.now(Clock.systemUTC());
              feature.getProperties().put(DYNACORE_ERRORS, "none");
              feature.getProperties().put(OUR_RETRIEVAL, retrieved.toString());
              if (!feature.getProperties().containsKey(TRAVEL_TIME)) {
                  feature.getProperties().put(TRAVEL_TIME, -1);
              }
              if (!feature.getProperties().containsKey(VELOCITY)) {
                  feature.getProperties().put(VELOCITY, -1);
              }
              if (!feature.getProperties().containsKey(LENGTH)) {
                  feature.getProperties().put(LENGTH, -1);
              }
              if (feature.getProperties().containsKey(ID)) {
                  feature.setId((String) feature.getProperties().get(ID));  // See RFC: If used then id SHOULD be included as a member of the Feature
                  feature.getProperties().remove(ID);                      // (ie. not as a member of properties)
              }
         } catch (Exception e) {
              e.printStackTrace();
         }
         return feature;
    }
}
