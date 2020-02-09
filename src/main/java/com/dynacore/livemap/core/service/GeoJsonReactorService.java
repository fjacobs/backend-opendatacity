package com.dynacore.livemap.core.service;

import com.dynacore.livemap.core.FeatureRequest;
import com.dynacore.livemap.core.adapter.GeoJsonAdapter;
import com.dynacore.livemap.core.model.TrafficDTO;
import com.dynacore.livemap.core.model.TrafficFeature;
import com.dynacore.livemap.core.model.TrafficFeatureInterface;
import com.dynacore.livemap.core.repository.TrafficEntity;
import com.dynacore.livemap.core.repository.TrafficRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.geojson.FeatureCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;

public abstract class GeoJsonReactorService<T extends TrafficEntity, R extends TrafficFeatureInterface, D extends TrafficDTO> {

  protected Flux<R> importedFlux;
  protected final GeoJsonAdapter retriever;
  protected final ServiceConfiguration config;
  protected final TrafficRepository<T> repo;
  private final EntityDistinct<T, D> entityDistinct;
  private final FeatureDistinct<R, R> featureDistinct;
  protected final FeatureImporter<R> featureImporter;

  private final Logger logger = LoggerFactory.getLogger(GeoJsonReactorService.class);

  public GeoJsonReactorService(
          ServiceConfiguration generalConfig,
          GeoJsonAdapter geoJsonAdapter,
          FeatureImporter<R> featureImporter,
          TrafficRepository<T> repo,
          EntityDistinct<T, D> entityDistinct,
          FeatureDistinct<R, R>  featureDistinct
          )
      throws JsonProcessingException {
    this.retriever = geoJsonAdapter;
    this.config = generalConfig;
    this.repo = repo;
    this.entityDistinct = entityDistinct;
    this.featureDistinct = featureDistinct;
    this.featureImporter = featureImporter;

    logger.info(this.config.getRequestInterval().toString());
    importedFlux = importFlux().cache(generalConfig.getRequestInterval());
  }

  protected Flux<R> importFlux() throws JsonProcessingException {
    return retriever
            .adapterHotSourceReq(config.getRequestInterval())
            .doOnNext(
                    x -> {
                      logger.info("Retrieved new featurecollection, size: " + x.getFeatures().size());
                      // System.out.println(x.getFeatures().get(0));
                    })
            .flatMapIterable(FeatureCollection::getFeatures)
            .map(featureImporter::importFeature);
  }

  public Flux<TrafficFeature> getFeatureRange(FeatureRequest range) {
    return repo.getFeatureDateRange(range.getStartDate(), range.getEndDate())
        .map(TrafficFeature::new);
  }

  public Flux<R> getLiveData() {
    return Flux.from(importedFlux).handle(featureDistinct.getFilter());
  }

  public Flux<List<D>> replayHistoryGroup(Duration interval) {
     return repo.getAllAscending()
        .handle(entityDistinct.filter())
        .windowUntilChanged(D::getPubDate)
        .flatMap(Flux::buffer)
        .delayElements(interval)
        .doOnNext(x -> System.out.println("Amount of distinct features: " + x.size()));
  }
}
