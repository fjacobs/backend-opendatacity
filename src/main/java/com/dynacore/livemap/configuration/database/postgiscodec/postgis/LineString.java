package com.dynacore.livemap.configuration.database.postgiscodec.postgis;



public class LineString extends Geometry {

    org.locationtech.jts.geom.LineString lineString;


    public LineString(int dimension, boolean hasMeasure, int srid) {
        super(dimension, hasMeasure, srid);
    }

    @Override
    public int getType() {
        return 2;
    }

    public void setLineString(org.locationtech.jts.geom.LineString lineString) {
        this.lineString = lineString;
    }

    public org.locationtech.jts.geom.LineString getLineString() {
        return  lineString;
    }

}
