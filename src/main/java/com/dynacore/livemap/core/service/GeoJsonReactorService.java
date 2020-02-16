package com.dynacore.livemap.core.service;

import com.dynacore.livemap.core.EntityToDtoMapper;
import com.dynacore.livemap.core.FeatureRequest;
import com.dynacore.livemap.core.adapter.GeoJsonAdapter;
import com.dynacore.livemap.core.model.TrafficDTO;
import com.dynacore.livemap.core.model.TrafficFeatureImpl;
import com.dynacore.livemap.core.model.TrafficFeature;
import com.dynacore.livemap.core.repository.TrafficEntity;
import com.dynacore.livemap.core.repository.TrafficRepository;
import com.dynacore.livemap.traveltime.domain.TravelTimeMapDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.geojson.FeatureCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.List;

public abstract class GeoJsonReactorService<
    T extends TrafficEntity, R extends TrafficFeature, D extends TrafficDTO> {

  protected Flux<R> importedFlux;
  protected final GeoJsonAdapter geoJsonAdapter;
  protected final ServiceProperties generalConfig;
  protected final TrafficRepository<T> repo;
  private final DTODistinctInterface<T, D> DTODistinctInterface;
  private final FeatureDistinct<R, R> featureDistinct;
  protected final FeatureImporter<R> featureImporter;

  private final Logger logger = LoggerFactory.getLogger(GeoJsonReactorService.class);

  public GeoJsonReactorService(
      ServiceProperties generalConfig,
      ObjectProvider<GeoJsonAdapter> geoJsonAdapterObjectProvider,
      FeatureImporter<R> featureImporter,
      TrafficRepository<T> repo,
      DTODistinctInterface<T, D> DTODistinctInterface,
      FeatureDistinct<R, R> featureDistinct)
      throws JsonProcessingException {

    this.geoJsonAdapter =
        geoJsonAdapterObjectProvider.getIfAvailable(() -> (serviceConfig) -> Flux.empty());
    this.generalConfig = generalConfig;
    this.repo = repo;
    this.DTODistinctInterface = DTODistinctInterface;
    this.featureDistinct = featureDistinct;
    this.featureImporter = featureImporter;

    Flux<FeatureCollection> emptyFlux = Flux.empty();

    logger.info(this.generalConfig.getRequestInterval().toString());
    importedFlux = importFlux().cache(generalConfig.getRequestInterval());
  }

  protected Flux<R> importFlux() throws JsonProcessingException {
    return geoJsonAdapter
        .adapterHotSourceReq(generalConfig.getRequestInterval())
        //        .doOnNext(
        //            x -> {
        //              logger.info("Retrieved new featurecollection, size: " +
        // x.getFeatures().size());
        //            })
        .flatMapIterable(FeatureCollection::getFeatures)
        .map(featureImporter::importFeature);
  }

  public Flux<TrafficFeatureImpl> getFeatureRange(FeatureRequest range) {
    return repo.getFeatureDateRange(range.getStartDate(), range.getEndDate())
        .map(TrafficFeatureImpl::new);
  }

  public Flux<R> getLiveData() {
    return Flux.from(importedFlux).handle(featureDistinct.getFilter());
  }

  public Flux<List<D>> replayHistoryGroup(Duration interval) {
    return repo.getAllAscending()
        .handle(DTODistinctInterface.filter())
        .windowUntilChanged(D::pubDate)
        .flatMap(Flux::buffer)
        .delayElements(interval)
        .doOnNext(x -> logger.info("Amount of distinct features: " + x.size()));
  }
}
