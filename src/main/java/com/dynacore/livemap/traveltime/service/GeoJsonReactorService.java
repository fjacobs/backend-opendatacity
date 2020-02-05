package com.dynacore.livemap.traveltime.service;

import com.dynacore.livemap.core.geojson.GeoJsonObjectVisitorWrapper;
import com.dynacore.livemap.core.geojson.TrafficFeature;
import com.dynacore.livemap.core.model.TrafficDTO;
import com.dynacore.livemap.core.service.GeoJsonAdapter;
import com.dynacore.livemap.core.service.ServiceConfiguration;
import com.dynacore.livemap.traveltime.domain.TravelTimeDTO;
import com.dynacore.livemap.traveltime.repo.TrafficRepository;
import com.dynacore.livemap.traveltime.repo.TravelTimeEntity;
import com.dynacore.livemap.traveltime.service.filter.FeatureRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.geojson.Feature;
import org.geojson.FeatureCollection;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.SynchronousSink;
import reactor.core.scheduler.Schedulers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

public abstract class GeoJsonReactorService {

  protected Flux<TrafficFeature> sharedFlux;
  protected final GeoJsonAdapter retriever;
  protected final ServiceConfiguration config;
  protected final TrafficRepository repo;

  Map<String, Integer> geoJsonStore = new HashMap<>();
  Map<String, TrafficDTO> dtoStore = new HashMap<>();

  @Autowired ModelMapper modelMapper;
  private final Logger logger = LoggerFactory.getLogger(GeoJsonReactorService.class);

  @Autowired
  public GeoJsonReactorService(
          TrafficRepository repo, GeoJsonAdapter retriever, ServiceConfiguration config)
      throws JsonProcessingException {
    this.retriever = retriever;
    this.config = config;
    this.repo = repo;
    logger.info(this.config.getRequestInterval().toString());

    sharedFlux = processFlux().cache(config.getRequestInterval());

    if (config.isSaveToDbEnabled()) {
      Flux.from(sharedFlux)
          .parallel(Runtime.getRuntime().availableProcessors())
          .runOn(Schedulers.parallel())
          .map(feature -> repo.save(modelMapper.map(feature, TravelTimeEntity.class)))
          .subscribe(Mono::subscribe, error -> logger.error("Error: " + error));
    }
  }

  protected abstract BiConsumer<? super TrafficFeature, SynchronousSink<TrafficFeature>> liveUpdateFilter();
  protected abstract BiConsumer<? super TrafficDTO, SynchronousSink<TrafficDTO>> replayDtoFilter();
  protected abstract GeoJsonObjectVisitorWrapper<Feature> processFeature();

  // DB independent replay
  protected Flux<List<TrafficDTO>> filterFlux(
          Flux<TrafficDTO> featureFlux,
          BiConsumer<? super TrafficDTO, SynchronousSink<TrafficDTO>> passThroughFilter) {

    // This can also be done with groupBy/concatMap (instead of
    // handle/windowUntilChanged/buffer):
    return featureFlux
            .handle(passThroughFilter)
            .windowUntilChanged(TrafficDTO::getPubDate)
            .flatMap(Flux::buffer)
            .doOnNext(x -> System.out.println("Amount of features changed: " + x.size()));
  }

  public Flux<TrafficFeature> getFeatureRange(FeatureRequest range) {
    return repo.getFeatureDateRange(range.getStartDate(), range.getEndDate())
        .map(entity -> modelMapper.map(entity, TrafficFeature.class));
  }

  public Flux<TrafficFeature> processFlux() throws JsonProcessingException {
    return retriever
            .requestHotSourceFc(config.getRequestInterval())
            .doOnNext(
                    x -> logger.info("Retrieved new featurecollection, size: " + x.getFeatures().size()))
            .flatMapIterable(FeatureCollection::getFeatures)
            .map(road -> ((TrafficFeature) road.accept(processFeature())));
  }

  public Flux<TrafficFeature> getLiveData() {
    return Flux.from(sharedFlux).handle(liveUpdateFilter());
  }

  public Flux<List<TrafficDTO>> replayHistoryGroup() {
    Flux<TrafficDTO> trafficDTOFlux =
        repo.getAllAscending().map(feature -> modelMapper.map(feature, TravelTimeDTO.class));
    return filterFlux(trafficDTOFlux, replayDtoFilter());
  }
  public Mono<FeatureCollection> getFeatureCollection() {
    return Flux.from(sharedFlux)
        .collectList()
        .map(
            list -> {
              FeatureCollection fc = new FeatureCollection();
              list.forEach(
                  feature -> {
                    list.add(TrafficFeature.class.cast(feature));
                  });
              return fc;
            });
  }

}
