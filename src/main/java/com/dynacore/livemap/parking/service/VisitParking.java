package com.dynacore.livemap.parking.service;

import com.dynacore.livemap.core.model.GeoJsonObjectVisitorWrapper;
import org.geojson.Feature;

public class VisitParking extends GeoJsonObjectVisitorWrapper<Feature> {

  @Override
  public Feature visit(Feature feature) {

    return (Feature) feature;
  }
}
