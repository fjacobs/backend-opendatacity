package com.dynacore.livemap.configuration.database.postgiscodec.postgis.binary;

import com.dynacore.livemap.configuration.database.postgiscodec.postgis.GeometryTypes;
import org.locationtech.jts.geom.*;
import org.locationtech.jts.geom.impl.CoordinateArraySequenceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class BinaryParser {

  static Logger logger = LoggerFactory.getLogger(BinaryParser.class);
  static GeometryFactory geometryFactory = new GeometryFactory();

  public static Geometry parseGeometry(final ValueGetter data) {
    final int typeWord = data.readInt();
    final int realType = typeWord & 0x1FFFFFFF; // cut off high flag bits
    final boolean haveZ = (typeWord & 0x80000000) != 0;
    final boolean haveM = (typeWord & 0x40000000) != 0;
    final boolean haveS = (typeWord & 0x20000000) != 0;
    final int srid;

    if (!haveS) {
      srid = GeometryTypes.UNKNOWN_SRID;
    } else {
      srid = data.readInt();
    }

    logger.info("SRID = " + srid);

    if (realType == GeometryTypes.POINT) {
      return parsePoint(data,  haveZ, haveM);
    }
    if (realType == GeometryTypes.LINESTRING) {
      return parseLineString(data, haveZ, haveM);
    }
    throw new RuntimeException("Type "  + realType + " not yet implemented in Postgis Extension codec.(Add to PostgisCodec BinaryParser)");
  }

  private static Geometry parsePoint(ValueGetter data, boolean haveZ, boolean haveM) {
    double X = data.readDouble();
    double Y = data.readDouble();
    Point result;
    if (haveZ) {
      double Z = data.readDouble();
      result = geometryFactory.createPoint(new Coordinate(X, Y, Z));
    } else {
      result = geometryFactory.createPoint(new Coordinate(X, Y));
    }

    if (haveM) { // skip M value
      data.readDouble();
    }

    return result;
  }

  private static Geometry parseLineString(final ValueGetter data, final boolean haveZ, final boolean haveM) {
    return geometryFactory.createLineString(parseCS(data,haveZ,haveM));
  }

  /**
   * Parse an Array of "slim" Points (without endianness and type, part of
   * LinearRing and Linestring, but not MultiPoint!
   *
   * @param haveZ
   * @param haveM
   */
  private static CoordinateSequence parseCS(ValueGetter data, boolean haveZ, boolean haveM) {
    int count = data.readInt();
    int dims = haveZ ? 3 : 2;

    CoordinateSequence cs = CoordinateArraySequenceFactory.instance().create(count,dims);

    for (int i = 0; i < count; i++) {
      for (int d = 0; d < dims; d++) {
        cs.setOrdinate(i, d, data.readDouble());
      }
      if (haveM) { // skip M value
        data.readDouble();
      }
    }

    return cs;
  }
}
