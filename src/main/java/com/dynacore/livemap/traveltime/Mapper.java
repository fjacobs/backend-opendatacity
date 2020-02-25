package com.dynacore.livemap.traveltime;

import org.geojson.Feature;
import org.geojson.LngLatAlt;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.PrecisionModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

public class Mapper {
    static PrecisionModel precisionModel = new PrecisionModel();
    static GeometryFactory geometryFactory = new GeometryFactory(precisionModel);
    static Logger log =  LoggerFactory.getLogger(Mapper.class);


    public static org.geojson.LineString lineStringConvertor(LineString in) {
        org.geojson.LineString lineString = new org.geojson.LineString();

        Arrays.stream(in.getCoordinateSequence().toCoordinateArray()).forEach(coord-> {
            lineString.add(new LngLatAlt(in.getCoordinate().getX(),  in.getCoordinate().getY()));
        });
        return lineString;
    }

    public static Feature convertToGeojson(String id, org.geojson.LineString geoJsonGeometry) {
        Feature feature = new Feature();
        feature.setId(id);
        feature.setGeometry( geoJsonGeometry);
        feature.setProperty("Velocity", 120);
        return feature;
    }
}
