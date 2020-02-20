package com.dynacore.livemap.configuration.database.postgiscodec.postgis;

public abstract class GeometryTypes {
  public static final int LINEARRING = 0;
  public static final int POINT = 1;
  public static final int LINESTRING = 2;
  public static final int POLYGON = 3;
  public static final int MULTIPOINT = 4;
  public static final int MULTILINESTRING = 5;
  public static final int MULTIPOLYGON = 6;
  public static final int GEOMETRYCOLLECTION = 7;

  public final static int UNKNOWN_SRID = 0;
}
