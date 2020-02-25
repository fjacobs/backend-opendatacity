package com.dynacore.livemap.core.service;

import com.dynacore.livemap.core.FeatureRequest;
import com.dynacore.livemap.core.adapter.GeoJsonAdapter;
import com.dynacore.livemap.core.model.TrafficDTO;
import com.dynacore.livemap.core.model.TrafficFeature;
import com.dynacore.livemap.core.model.TrafficFeatureImpl;
import com.dynacore.livemap.core.repository.GeometryEntity;
import com.dynacore.livemap.core.repository.TrafficEntity;
import com.dynacore.livemap.core.repository.TrafficRepository;
import com.dynacore.livemap.traveltime.controller.LatLngBounds;
import com.dynacore.livemap.traveltime.repo.TravelTimeRepo;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.geojson.FeatureCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.r2dbc.core.DatabaseClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Hooks;
import reactor.core.publisher.Mono;

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
      FeatureImporter<R> featureMapper,
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
    this.featureImporter = featureMapper;

    logger.info(this.generalConfig.getRequestInterval().toString());
 //   importedFlux = importFlux().cache(generalConfig.getRequestInterval());
    importedFlux = importFlux();
  }

  protected Flux<R> importFlux() throws JsonProcessingException {
    Hooks.onOperatorDebug();
    return geoJsonAdapter
        .adapterHotSourceReq(generalConfig.getRequestInterval())
        .doOnNext(
            x -> {
              logger.info("Retrieved new fc, size: " + x.getFeatures().size());
            })
        .flatMapIterable(FeatureCollection::getFeatures)
        .map(featureImporter::importFeature);
  }

  public Flux<TrafficFeatureImpl> getFeatureRange(FeatureRequest range) {
    return repo.getReplayData(range.startDate(), range.direction())
        .map(TrafficFeatureImpl::new);
  }

  public Flux<R> getLiveData() {
     return Flux.from(importedFlux).handle(featureDistinct.getFilter()).log();
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
