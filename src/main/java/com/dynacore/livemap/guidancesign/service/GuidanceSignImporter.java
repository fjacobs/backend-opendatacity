package com.dynacore.livemap.guidancesign.service;

import com.dynacore.livemap.core.service.FeatureImporter;
import com.dynacore.livemap.guidancesign.domain.GuidanceSignFeature;
import org.geojson.Feature;
import org.springframework.stereotype.Component;


@Component
public class GuidanceSignImporter implements FeatureImporter<GuidanceSignFeature> {


  @Override
  public GuidanceSignFeature importFeature(Feature feature) {

    System.out.println(feature);
    return new GuidanceSignFeature(feature);
  }
}
