package com.dynacore.livemap.traveltime.service;

import com.dynacore.livemap.core.model.GeoJsonObjectVisitorWrapper;
import com.dynacore.livemap.traveltime.domain.TravelTimeFeature;
import org.geojson.Feature;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import static com.dynacore.livemap.core.model.TrafficFeature.ID;
import static com.dynacore.livemap.core.model.TrafficFeature.OUR_RETRIEVAL;
import static com.dynacore.livemap.traveltime.domain.TravelTimeFeature.*;

public class VisitTravelTime extends GeoJsonObjectVisitorWrapper<Feature> {

  @Override
  public Feature visit(Feature feature) {
    TravelTimeFeature travelTimeFeature;
    if (feature instanceof TravelTimeFeature) {
       travelTimeFeature = (TravelTimeFeature) feature;
    } else {
      travelTimeFeature = new TravelTimeFeature(feature);
    }
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
    } catch (Exception e) {
      e.printStackTrace();
    }
    return travelTimeFeature;
  }
}
