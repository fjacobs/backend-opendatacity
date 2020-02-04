package com.dynacore.livemap.traveltime.service.visitor;

import com.dynacore.livemap.core.geojson.GeoJsonObjectVisitorWrapper;
import org.geojson.*;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

public class CalculateTravelTime extends GeoJsonObjectVisitorWrapper<Feature> {

  private static final String ID = "Id";
  private static final String TRAVEL_TIME = "Traveltime";
  private static final String LENGTH = "Length";
  private static final String VELOCITY = "Velocity";
  private static final String OUR_RETRIEVAL = "retrievedFromThirdParty";
  private static final String DYNACORE_ERRORS = "dynacoreErrors";

  @Override
  public Feature visit(Feature feature) {
    try {
      OffsetDateTime retrieved = OffsetDateTime.now(ZoneOffset.UTC);
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
        feature.setId(
            (String)
                feature.getProperties().get(ID)); // See RFC 7946: If an ID is used then it SHOULD
        // be included as a top level member
        feature.getProperties().remove(ID); // and not as a member of properties
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return feature;
  }
}
