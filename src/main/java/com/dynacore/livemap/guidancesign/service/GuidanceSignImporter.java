package com.dynacore.livemap.guidancesign.service;

import com.dynacore.livemap.core.service.FeatureImporter;
import com.dynacore.livemap.guidancesign.domain.GuidanceSignFeatureImpl;
import org.geojson.Feature;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;

import static com.dynacore.livemap.core.model.TrafficFeature.OUR_CREATION_DATE;

@Component
public class GuidanceSignImporter implements FeatureImporter<GuidanceSignFeatureImpl> {

  @Override
  public GuidanceSignFeatureImpl importFeature(Feature feature) {
    feature.setProperty(OUR_CREATION_DATE, OffsetDateTime.now());
    return new GuidanceSignFeatureImpl(feature);
  }
}
