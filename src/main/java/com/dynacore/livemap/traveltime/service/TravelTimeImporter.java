package com.dynacore.livemap.traveltime.service;

import com.dynacore.livemap.core.service.FeatureImporter;
import com.dynacore.livemap.traveltime.domain.TravelTimeFeatureImpl;
import org.geojson.Feature;
import org.springframework.stereotype.Component;

@Component
public class TravelTimeImporter implements FeatureImporter<TravelTimeFeatureImpl> {

  @Override
  public TravelTimeFeatureImpl importFeature(Feature feature) {

    TravelTimeFeatureImpl travelTimeFeature = null;

    try {
      travelTimeFeature = new TravelTimeFeatureImpl(feature);

    } catch (Exception e) {
        System.err.println("Could not import feature: " + feature);
      assert false;
    }

    return travelTimeFeature;
  }
}
