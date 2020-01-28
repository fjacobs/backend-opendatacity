/*
 * Copyright 2002-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.dynacore.livemap.traveltime.service;

import com.dynacore.livemap.core.ReactiveGeoJsonPublisher;
import com.dynacore.livemap.core.service.GeoJsonAdapter;
import com.dynacore.livemap.traveltime.domain.RoadFeature;
import com.dynacore.livemap.traveltime.repo.TravelTimeEntity;
import com.dynacore.livemap.traveltime.repo.TravelTimeRepo;
import com.dynacore.livemap.traveltime.service.filter.FeatureRequest;
import com.dynacore.livemap.traveltime.service.visitor.CalculateTravelTime;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.geojson.Feature;
import org.geojson.FeatureCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Road traffic traveltime service
 * <p>
 * This service will subscribe to a traffic information data source.
 * It checks the GeoJson for RFC compliance and calculates properties and publishes the new data in a reactive manner.
 * The data is automatically saved in a reactive database (R2DBC) and the last emitted signals are cached
 * for new subscribers.
 */

@Lazy(false)
@Profile("traveltime")
@Service("travelTimeService")
public class TravelTimeService implements ReactiveGeoJsonPublisher {

    private Flux<Feature> sharedFlux;
    private final GeoJsonAdapter retriever;
    private final TravelTimeServiceConfig config;
    private final TravelTimeRepo repo;
    private final Logger log = LoggerFactory.getLogger(TravelTimeService.class);
    Map<String, Integer> store = new HashMap<>();

    @Autowired
    public TravelTimeService(TravelTimeRepo repo, GeoJsonAdapter retriever, TravelTimeServiceConfig config) throws JsonProcessingException {
        this.retriever = retriever;
        this.config = config;
        this.repo = repo;

        log.info(this.config.getRequestInterval().toString());

        sharedFlux = processFlux()
                .cache(config.getRequestInterval());

        if (config.isSaveToDbEnabled()) {
            Flux.from(sharedFlux)
                    .parallel(Runtime.getRuntime().availableProcessors())
                    .runOn(Schedulers.parallel())
                    .map(feature -> repo.save(new TravelTimeEntity((RoadFeature)feature)))
                    .subscribe(Mono::subscribe, error -> log.error("Error: " + error));
        }
    }

    private Flux<Feature> processFlux() throws JsonProcessingException {
        return retriever
                .requestHotSourceFc(config.getRequestInterval())
                .doOnNext(x -> System.out.println("Retrieved new featurecollection, size: " + x.getFeatures().size()))
                .flatMapIterable(FeatureCollection::getFeatures)
                .map(road -> road.accept(new CalculateTravelTime()));
    }

    public Flux<Feature> getLiveData() {
        return Flux.from(sharedFlux)
                .handle((feature, sink) -> {
                    Integer newHash = DistinctUtil.hashCodeNoRetDate.apply(feature);
                    Integer oldHash;
                    if ((oldHash = store.get(feature.getId())) != null) {
                        if (!newHash.equals(oldHash)) {
                            store.put(feature.getId(), newHash);
                            sink.next(feature);
                        }
                    } else {
                        store.put(feature.getId(), newHash);
                        sink.next(feature);
                    }
                });
    }

    public Flux<TravelTimeEntity> getFeatureRange(FeatureRequest range) {
        return repo.getFeatureDateRange(range.getStartDate(), range.getEndDate());
    }

    public Flux<List<TravelTimeEntity>> replayGroupedByPubdate(Integer delay) {
        return repo.getAllAscending()
                   .windowUntilChanged(TravelTimeEntity::getPubDate)
                   .flatMap(Flux::buffer)
                   .delayElements(Duration.ofSeconds(delay));
    }

    public Mono<FeatureCollection> getFeatureCollection() {
        return Flux.from(sharedFlux)
                .collectList()
                .map(features -> {
                    FeatureCollection fc = new FeatureCollection();
                    fc.setFeatures(features);
                    return fc;
                });
    }
}
