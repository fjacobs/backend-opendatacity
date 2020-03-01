package com.dynacore.livemap.core.service;

import com.dynacore.livemap.core.Direction;
import com.dynacore.livemap.core.adapter.GeoJsonAdapter;
import com.dynacore.livemap.core.model.TrafficDTO;
import com.dynacore.livemap.core.model.TrafficFeature;
import com.dynacore.livemap.core.repository.TrafficEntity;
import com.dynacore.livemap.core.repository.TrafficRepository;
import com.dynacore.livemap.traveltime.domain.TravelTimeMapDTO;
import com.dynacore.livemap.traveltime.repo.TravelTimeEntityImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.geojson.FeatureCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.lang.NonNull;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Hooks;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.function.Function;

import static org.springframework.data.r2dbc.query.Criteria.where;

public abstract class GeoJsonReactorService<
    T extends TrafficEntity, R extends TrafficFeature, D extends TrafficDTO> {

  protected final GeoJsonAdapter geoJsonAdapter;
  protected final ServiceProperties generalConfig;
  protected final TrafficRepository<T> repo;
  protected final DTODistinctInterface<T, D> dtoDistinctInterface;
  protected final FeatureImporter<R> featureImporter;
  private final FeatureDistinct<R, R> featureDistinct;
  private final Logger log = LoggerFactory.getLogger(GeoJsonReactorService.class);
  protected Flux<R> importedFeatures;

  public GeoJsonReactorService(
      ServiceProperties config,
      ObjectProvider<GeoJsonAdapter> geoJsonAdapterObjectProvider,
      FeatureImporter<R> featureMapper,
      TrafficRepository<T> repo,
      DTODistinctInterface<T, D> dtoDistinctInterface,
      FeatureDistinct<R, R> featureDistinct)
      throws JsonProcessingException {

    this.geoJsonAdapter =
        geoJsonAdapterObjectProvider.getIfAvailable(() -> (serviceConfig) -> Flux.empty());
    this.generalConfig = config;
    this.repo = repo;
    this.dtoDistinctInterface = dtoDistinctInterface;
    this.featureDistinct = featureDistinct;
    this.featureImporter = featureMapper;

    log.info(this.generalConfig.getRequestInterval().toString());
    importedFeatures = importFlux().cache(config.getRequestInterval());
  }

  protected Flux<R> importFlux() throws JsonProcessingException {
    Hooks.onOperatorDebug();
    return geoJsonAdapter
        .adapterHotSourceReq(generalConfig.getRequestInterval())
       // .doOnNext(x -> log.info("Imported external fc of size: " + x.getFeatures().size()))
        .flatMapIterable(FeatureCollection::getFeatures)
        .map(featureImporter::importFeature);
  }

//  public Flux<List<D>> replayHistory(
//      OffsetDateTime startDate, Direction direction, @NonNull Duration interval) {
//
//    log.info(
//        "Service replayv2. startdate: {}, Direction:  {}, Interval {}",
//        startDate,
//        direction,
//        interval);
//
//    return repo.getReplayData(startDate, direction)
//        .handle(dtoDistinctInterface.filter())
//        .bufferUntilChanged(TrafficDTO::pubDate)
//        .delayElements(interval);
//  }

  public Flux<R> getLiveData() {
    return Flux.from(importedFeatures).handle(featureDistinct.getFilter());
  }

  @Deprecated
  public Flux<List<D>> replayHistoryGroup(Duration interval) {
    return repo.getAllAscending()
        .handle(dtoDistinctInterface.filter())
        .windowUntilChanged(D::pubDate)
        .flatMap(Flux::buffer)
        .delayElements(interval)
        .doOnNext(features -> log.info("Distinct feature count: " + features.size()));
  }
}
