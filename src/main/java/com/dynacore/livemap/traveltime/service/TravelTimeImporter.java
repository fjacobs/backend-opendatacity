package com.dynacore.livemap.traveltime.service;

import com.dynacore.livemap.core.service.FeatureImporter;
import com.dynacore.livemap.traveltime.domain.TravelTimeFeature;
import org.geojson.Feature;
import org.springframework.stereotype.Component;

@Component
public class TravelTimeImporter implements FeatureImporter<TravelTimeFeature> {

  @Override
  public TravelTimeFeature importFeature(Feature feature) {

    TravelTimeFeature travelTimeFeature = null;

    try {
      travelTimeFeature = new TravelTimeFeature(feature);

    } catch (Exception e) {
      System.err.println("Could not import feature: " + feature);
      assert false;
    }

    return travelTimeFeature;
  }
}
