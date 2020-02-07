package com.dynacore.livemap.core.service;

import com.dynacore.livemap.core.FeatureRequest;
import com.dynacore.livemap.core.adapter.GeoJsonAdapter;
import com.dynacore.livemap.core.model.GeoJsonObjectVisitorWrapper;
import com.dynacore.livemap.core.model.TrafficFeature;
import com.dynacore.livemap.core.model.TrafficDTO;
import com.dynacore.livemap.core.repository.TrafficEntity;
import com.dynacore.livemap.core.repository.TrafficRepository;
import com.dynacore.livemap.core.service.configuration.FeatureFilter;
import com.dynacore.livemap.core.service.configuration.RepoFilter;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.geojson.Feature;
import org.geojson.FeatureCollection;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public abstract class GeoJsonReactorService {

  protected Flux<TrafficFeature> sharedFlux;
  protected final GeoJsonAdapter retriever;
  protected final ServiceConfiguration config;
  protected final TrafficRepository<? extends TrafficEntity> repo;
  private final RepoFilter repoFilter;
  private final FeatureFilter featureFilter;

  @Autowired ModelMapper modelMapper;
  private final Logger logger = LoggerFactory.getLogger(GeoJsonReactorService.class);

  @Autowired
  public GeoJsonReactorService(
          GeoJsonAdapter retriever,
          TrafficRepository<? extends TrafficEntity> repo,
          ServiceConfiguration generalConfig,
          RepoFilter repoFilter,
          FeatureFilter featureFilter)
      throws JsonProcessingException {
    this.retriever = retriever;
    this.config = generalConfig;
    this.repo = repo;
    this.repoFilter = repoFilter;
    this.featureFilter = featureFilter;

    logger.info(this.config.getRequestInterval().toString());

    sharedFlux = processFlux().cache(generalConfig.getRequestInterval());

    //    if (generalConfig.isSaveToDbEnabled()) {
    //      Flux.from(sharedFlux)
    //          .parallel(Runtime.getRuntime().availableProcessors())
    //          .runOn(Schedulers.parallel())
    //          .map(feature -> repo.save(modelMapper.map(feature, ParkingEntity.class)))
    //          .subscribe(Mono::subscribe, error -> logger.error("Error: " + error));
    //    }
  }

  public Flux<TrafficFeature> processFlux() throws JsonProcessingException {
    return retriever
            .requestHotSourceFc(config.getRequestInterval())
            .doOnNext(
                    x -> logger.info("Retrieved new featurecollection, size: " + x.getFeatures().size())

            )
            .flatMapIterable(FeatureCollection::getFeatures)
            .map(feature -> ((TrafficFeature) feature.accept(processFeature())));
  }

  protected abstract GeoJsonObjectVisitorWrapper<Feature> processFeature();

  public Flux<TrafficFeature> getFeatureRange(FeatureRequest range) {
    return repo.getFeatureDateRange(range.getStartDate(), range.getEndDate())
        .map(entity -> modelMapper.map(entity, TrafficFeature.class));
  }



  public Flux<TrafficFeature> getLiveData() {
    return Flux.from(sharedFlux).handle(featureFilter.filter());
  }

  public Flux<List<TrafficDTO>> replayHistoryGroup() {
    return repo.getAllAscending()
        .handle(repoFilter.filter())
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
              list.forEach(fc::add);
              return fc;
            });
  }
}
