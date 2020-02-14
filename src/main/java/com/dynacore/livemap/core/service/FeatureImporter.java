package com.dynacore.livemap.core.service;

import com.dynacore.livemap.core.model.TrafficFeature;
import org.geojson.Feature;

public interface FeatureImporter<T extends TrafficFeature> {

  T  importFeature(Feature feature);
}
