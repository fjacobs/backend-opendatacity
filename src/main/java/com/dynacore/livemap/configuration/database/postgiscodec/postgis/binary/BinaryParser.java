package com.dynacore.livemap.configuration.database.postgiscodec.postgis.binary;


import com.dynacore.livemap.configuration.database.postgiscodec.postgis.Geometry;
import com.dynacore.livemap.configuration.database.postgiscodec.postgis.Point;
import org.locationtech.jts.geom.CoordinateSequence;
import org.locationtech.jts.geom.GeometryFactory;
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
   // geometryFactory.createLineString(data.)
    return null;
  }

  /**
   * Parse an Array of "slim" Points (without endianness and type, part of
   * LinearRing and Linestring, but not MultiPoint!
   *
   * @param haveZ
   * @param haveM
   */
  private CoordinateSequence parseCS(ValueGetter data, boolean haveZ, boolean haveM) {
//    int count = data.readInt();
//
//    double[] x = new double[count];
//
//
//
//    int dims = haveZ ? 3 : 2;
//    CoordinateSequence cs = new PackedCoordinateSequence.Double(x, dims);
//
//    for (int i = 0; i < count; i++) {
//      for (int d = 0; d < dims; d++) {
//        cs.setOrdinate(i, d, data.readDouble());
//      }
//      if (haveM) { // skip M value
//        data.readDouble();
//      }
//    }
//    return cs;
//  }
    return null;
  }
}
