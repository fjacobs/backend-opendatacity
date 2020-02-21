package com.dynacore.livemap.core.repository;

import com.dynacore.livemap.core.model.TrafficFeatureImpl;
import org.geojson.GeoJsonObject;
import org.locationtech.jts.geom.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EntityMapper {
    static PrecisionModel precisionModel = new PrecisionModel();
    static GeometryFactory geometryFactory = new GeometryFactory(precisionModel);
    static Logger log =  LoggerFactory.getLogger(EntityMapper.class);
    public static org.locationtech.jts.geom.LineString lineStringConvertor(org.geojson.LineString geometry) {

        Coordinate[] lineStringArr = geometry.getCoordinates().stream().map(lngLatAlt -> new Coordinate(lngLatAlt.getLongitude(), lngLatAlt.getLatitude(), lngLatAlt.getAltitude())).toArray(Coordinate[]::new);
        return geometryFactory.createLineString(lineStringArr);
    }

    public static org.locationtech.jts.geom.Point pointConvertor(org.geojson.Point geojsonGeom) {
        return geometryFactory.createPoint(new Coordinate(geojsonGeom.getCoordinates().getLatitude(), geojsonGeom.getCoordinates().getLongitude(), geojsonGeom.getCoordinates().getAltitude()));
    }

    public static GeometryEntity geometryEntityConvertor(TrafficFeatureImpl feature) {
        Geometry postGisGeom = null;
        GeoJsonObject geoJsonObject = feature.getGeometry();
        if (geoJsonObject instanceof org.geojson.LineString lineString) {
            postGisGeom = lineStringConvertor(lineString);
        }
        else if (geoJsonObject instanceof org.geojson.Point point) {
            postGisGeom = pointConvertor(point);
        }
       if (postGisGeom == null) throw new RuntimeException("No convertor available for " + feature.getGeometry().getClass());
        return new GeometryEntity(feature.getId(), feature.getClass().getSimpleName(), feature.getGeometry().getClass().getSimpleName(), postGisGeom);
    }
}
