package com.dynacore.livemap.guidancesign.service;

import com.dynacore.livemap.core.service.FeatureImporter;
import com.dynacore.livemap.guidancesign.domain.GuidanceSignFeature;
import org.geojson.Feature;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;

import static com.dynacore.livemap.core.model.TrafficFeatureInterface.OUR_CREATION_DATE;

@Component
public class GuidanceSignImporter implements FeatureImporter<GuidanceSignFeature> {

  @Override
  public  GuidanceSignFeature importFeature(Feature feature) {
    feature.setProperty(OUR_CREATION_DATE, OffsetDateTime.now());
    return new GuidanceSignFeature(feature);
  }
}
