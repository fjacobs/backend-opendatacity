package com.dynacore.livemap.traveltime.service;

import com.dynacore.livemap.core.service.FeatureImporter;
import com.dynacore.livemap.traveltime.domain.TravelTimeFeature;
import org.geojson.Feature;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import static com.dynacore.livemap.core.model.TrafficFeature.ID;
import static com.dynacore.livemap.core.model.TrafficFeature.OUR_RETRIEVAL;
import static com.dynacore.livemap.traveltime.domain.TravelTimeFeature.*;

@Component
public class TravelTimeImporter implements FeatureImporter<TravelTimeFeature> {

  @Override
  public TravelTimeFeature importFeature(Feature feature) {

    TravelTimeFeature travelTimeFeature = null;
    try {
      OffsetDateTime retrieved = OffsetDateTime.now(ZoneOffset.UTC);
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
        feature.setId(
                (String)
                        feature.getProperties().get(ID)); // See RFC 7946: If an ID is used then it SHOULD
        // be included as a top level member
        feature.getProperties().remove(ID); // and not as a member of properties
      }

      if (feature instanceof TravelTimeFeature) {
        travelTimeFeature = (TravelTimeFeature) feature;
      } else {
        travelTimeFeature = new TravelTimeFeature(feature);
      }
    } catch (Exception e) {
      System.err.println("Could not import feature: " + feature);
      assert false;
    }

    return travelTimeFeature;
  }
}
