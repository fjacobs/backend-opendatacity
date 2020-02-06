package com.dynacore.livemap.traveltime.service;

import com.dynacore.livemap.core.geojson.GeoJsonObjectVisitorWrapper;
import com.dynacore.livemap.core.geojson.TrafficFeature;
import com.dynacore.livemap.core.model.TrafficDTO;
import com.dynacore.livemap.core.service.configuration.DtoFilter;
import com.dynacore.livemap.core.service.configuration.FeatureFilter;
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
import reactor.core.scheduler.Schedulers;

import java.util.List;

public abstract class GeoJsonReactorService {

  protected Flux<TrafficFeature> sharedFlux;
  protected final GeoJsonAdapter retriever;
  protected final ServiceConfiguration config;
  protected final TrafficRepository repo;
  private final DtoFilter dtoFilter;
  private final FeatureFilter featureFilter;


  @Autowired ModelMapper modelMapper;
  private final Logger logger = LoggerFactory.getLogger(GeoJsonReactorService.class);

  @Autowired
  public GeoJsonReactorService(
      GeoJsonAdapter retriever,
      TrafficRepository repo,
      ServiceConfiguration generalConfig,
      DtoFilter dtoFilter,
      FeatureFilter featureFilter)
      throws JsonProcessingException {
    this.retriever = retriever;
    this.config = generalConfig;
    this.repo = repo;
    this.dtoFilter = dtoFilter;
    this.featureFilter = featureFilter;
    logger.info(this.config.getRequestInterval().toString());

    sharedFlux = processFlux().cache(generalConfig.getRequestInterval());

    if (generalConfig.isSaveToDbEnabled()) {
      Flux.from(sharedFlux)
          .parallel(Runtime.getRuntime().availableProcessors())
          .runOn(Schedulers.parallel())
          .map(feature -> repo.save(modelMapper.map(feature, TravelTimeEntity.class)))
          .subscribe(Mono::subscribe, error -> logger.error("Error: " + error));
    }
  }

  protected abstract GeoJsonObjectVisitorWrapper<Feature> processFeature();

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
    return Flux.from(sharedFlux).handle(featureFilter.filter());
  }

  public Flux<List<TrafficDTO>> replayHistoryGroup() {
        return repo.getAllAscending().map(feature -> modelMapper.map(feature, TravelTimeDTO.class))
               .handle(dtoFilter.filter())
               .windowUntilChanged(TrafficDTO::getPubDate)
               .flatMap(Flux::buffer)
               .doOnNext(x -> System.out.println("Amount of features changed: " + x.size()));
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
