package com.dynacore.livemap.traveltime;

import com.dynacore.livemap.traveltime.repo.TravelTimeEntity;
import org.geojson.Feature;
import org.geojson.FeatureCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.Clock;
import java.time.OffsetDateTime;

public interface TravelTimeService {
    String ID = "Id";
    String NAME = "Name";
    String TYPE = "Type";
    String TRAVEL_TIME = "Traveltime";
    String LENGTH = "Length";
    String VELOCITY = "Velocity";
    String OUR_RETRIEVAL = "retrievedFromThirdParty";
    String THEIR_RETRIEVAL = "Timestamp";
    String DYNACORE_ERRORS = "dynacoreErrors";
    String SOURCEURL = "http://web.redant.net/~amsterdam/ndw/data/reistijdenAmsterdam.geojson";
    int INTERVAL = 5;
    Logger logger = LoggerFactory.getLogger(TravelTimeService.class);

    default Flux<Feature> processFeatures(FeatureCollection fc) {
        return Flux.fromIterable(fc)
                .parallel(Runtime.getRuntime().availableProcessors())
                .runOn(Schedulers.parallel())
                .map(this::processFeature)
                .sequential()
                .share()
                .doOnSubscribe(s -> logger.info("Subscribed to processed features source"))
                .doOnComplete(() -> logger.info("Completed processing features"));
    }

    default Mono<FeatureCollection> convertToFeatureCollection(Flux<Feature> featureFlux) {
        return featureFlux.collectList()
                .map(features -> {
                    FeatureCollection fc = new FeatureCollection();
                    fc.setFeatures(features);
                    return fc;
                });
    }

    default Feature processFeature(Feature feature) {
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

    default TravelTimeEntity convertToEntity(Feature travelTime) {
        return new TravelTimeEntity.Builder()
                .id(travelTime.getId())
                .name((String) travelTime.getProperties().get(NAME))
                .pubDate((String) travelTime.getProperties().get(THEIR_RETRIEVAL))
                .retrievedFromThirdParty((String) travelTime.getProperties().get(OUR_RETRIEVAL))
                .type((String) travelTime.getProperties().get(TYPE))
                .travelTime((int) travelTime.getProperties().get(TRAVEL_TIME))
                .velocity((int) travelTime.getProperties().get(VELOCITY))
                .length((int) travelTime.getProperties().get(LENGTH)).build();
    }

}
