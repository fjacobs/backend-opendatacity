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
import com.dynacore.livemap.traveltime.repo.TravelTimeEntity;
import com.dynacore.livemap.traveltime.repo.TravelTimeRepo;
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

/**
     Road traffic traveltime service

     This service will subscribe to a traffic information data source.
     It checks the GeoJson for RFC compliance and calculates properties and publishes the new data in a reactive manner.
     The data is automatically saved in a reactive database (R2DBC) and the last emitted signals are cached
     for new subscribers
 */

@Lazy(false)
@Profile("traveltime")
@Service("travelTimeService")
public class TravelTimeService implements ReactiveGeoJsonPublisher {

    private Flux<Feature> sharedFlux;
    private final OpenDataRetriever retriever;
    private final TravelTimeServiceConfig config;
    private final Logger log = LoggerFactory.getLogger(TravelTimeService.class);

    @Autowired
    public TravelTimeService(TravelTimeRepo repo, OpenDataRetriever retriever, TravelTimeServiceConfig config) throws JsonProcessingException {

        this.retriever = retriever;
        this.config = config;

        sharedFlux = processFlux()
                    .cache(config.getRequestInterval());

        Flux.from(sharedFlux)
                .parallel(Runtime.getRuntime().availableProcessors())
                .runOn(Schedulers.parallel())
                .map(feature -> repo.save(new TravelTimeEntity((feature))))
                .subscribe( Mono::subscribe,
                            error -> log.error("Error: " + error) );
    }

    private Flux<Feature> processFlux() throws JsonProcessingException {

        return retriever
                .requestHotSourceFc(config.getRequestInterval())
                .doOnNext(x-> System.out.println("Retrieved new featurecollection, size: " + x.getFeatures().size()))
                .flatMapIterable(FeatureCollection::getFeatures)
                .distinct(DistinctUtil.hashCodeNoRetDate)
                .map(road -> road.accept(new CalculateTravelTime()));
    }

    public Flux<Feature> getFeatures() {
        return Flux.from(sharedFlux);
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
