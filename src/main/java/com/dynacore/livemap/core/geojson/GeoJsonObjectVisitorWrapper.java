package com.dynacore.livemap.core.geojson;

import com.dynacore.livemap.traveltime.service.TravelTimeService;
import org.geojson.*;
import org.geojson.GeoJsonObjectVisitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// Empty GeoJsonObjectVisitor, extend if you don't want to override methods that aren't used

public class GeoJsonObjectVisitorWrapper<T> implements GeoJsonObjectVisitor<T> {
  private final Logger logger = LoggerFactory.getLogger(TravelTimeService.class);

  @Override
  public T visit(Feature feature) {
    logger.warn("empty geojson object visitor: feature");
    return null;
  }

  @Override
  public T visit(FeatureCollection featureCollection) {
    logger.warn("empty geojson object visitor: featureCollection");
    return null;
  }

  @Override
  public T visit(GeometryCollection geometryCollection) {
    logger.warn("empty geojson object visitor: geometryCollection");
    return null;
  }

  @Override
  public T visit(LineString lineString) {
    logger.warn("empty geojson object visitor: lineString");
    return null;
  }

  @Override
  public T visit(MultiLineString multiLineString) {
    logger.warn("empty geojson object visitor: multiLineString");
    return null;
  }

  @Override
  public T visit(MultiPoint multiPoint) {
    logger.warn("empty geojson object visitor: multiPoint");
    return null;
  }

  @Override
  public T visit(MultiPolygon multiPolygon) {
    logger.warn("empty geojson object visitor: multiPolygon");
    return null;
  }

  @Override
  public T visit(Point point) {
    logger.warn("empty geojson object visitor: point");
    return null;
  }

  @Override
  public T visit(Polygon polygon) {
    logger.warn("empty geojson object visitor: polygon");
    return null;
  }
}
