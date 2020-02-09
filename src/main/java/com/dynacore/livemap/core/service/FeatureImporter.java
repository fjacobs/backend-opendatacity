package com.dynacore.livemap.core.service;

import com.dynacore.livemap.core.model.TrafficFeature;
import com.dynacore.livemap.core.model.TrafficFeatureInterface;
import org.geojson.Feature;

public interface FeatureImporter<T extends TrafficFeatureInterface> {

  T importFeature(Feature feature);
}
