package com.dynacore.livemap.configuration.database.postgiscodec.postgis.binary;


import com.dynacore.livemap.configuration.database.postgiscodec.postgis.Geometry;
import com.dynacore.livemap.configuration.database.postgiscodec.postgis.LineString;
import com.dynacore.livemap.configuration.database.postgiscodec.postgis.Point;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.CoordinateSequence;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.impl.CoordinateArraySequenceFactory;
import org.locationtech.jts.geom.impl.PackedCoordinateSequence;
import org.postgis.binary.BinaryParser.*;
public class BinaryParser {
  public static Geometry parseGeometry(final ValueGetter data) {
    final int typeWord = data.readInt();
    final int realType = typeWord & 0x1FFFFFFF; // cut off high flag bits
    final boolean haveZ = (typeWord & 0x80000000) != 0;
    final boolean haveM = (typeWord & 0x40000000) != 0;
    final boolean haveS = (typeWord & 0x20000000) != 0;
    final int srid;

    if (!haveS) {
      srid = Geometry.UNKNOWN_SRID;
    } else {
      srid = data.readInt();
    }

    if (realType == Geometry.POINT) {
      return parsePoint(data, srid, haveZ, haveM);
    }
    if (realType == Geometry.LINESTRING) {
      return parseLineString(data, srid, haveZ, haveM);
    }
    return null;
  }

  private static Geometry parsePoint(final ValueGetter data, final int srid, final boolean haveZ, final boolean haveM) {
    final double x = data.readDouble();
    final double y = data.readDouble();

    final double z;

    if (haveZ) {
      z = data.readDouble();
    } else {
      z = 0.0;
    }

    final double m;

    if (haveM) {
      m = data.readDouble();
    } else {
      m = 0.0;
    }

    if (!haveZ) {
      return Point.of2D(x, y, m, haveM, srid);
    } else {
      return Point.of3D(x, y, z, m, haveM, srid);
    }
  }

  private static Geometry parseLineString(final ValueGetter data, final int srid, final boolean haveZ, final boolean haveM) {
    GeometryFactory geometryFactory = new GeometryFactory();

    int dims = haveZ ? 3 : 2;
    LineString lineString = new LineString(dims,haveM,srid);
    lineString.setLineString(geometryFactory.createLineString(parseCS(data,haveZ,haveM)));
    return lineString;
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

//    LINESTRING(77.29 29.07,77.42 29.26)
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
