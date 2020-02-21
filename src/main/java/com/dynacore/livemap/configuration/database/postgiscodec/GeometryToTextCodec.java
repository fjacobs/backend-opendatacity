package com.dynacore.livemap.configuration.database.postgiscodec;

import com.dynacore.livemap.configuration.database.postgiscodec.binary.BinaryParser;
import com.dynacore.livemap.configuration.database.postgiscodec.binary.ByteGetter;
import com.dynacore.livemap.configuration.database.postgiscodec.binary.ValueGetter;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.r2dbc.postgresql.client.Parameter;
import io.r2dbc.postgresql.codec.Codec;
import io.r2dbc.postgresql.message.Format;
import org.locationtech.jts.geom.Geometry;

public class GeometryToTextCodec implements Codec<Geometry> {
  private final int oid;
  private final ByteBufAllocator allocator;

  public GeometryToTextCodec(final int oid, final ByteBufAllocator allocator) {
    this.oid = oid;
    this.allocator = allocator;
  }

  @Override
  public boolean canDecode(final int dataType, final Format format, final Class<?> type) {
    return dataType == this.oid;
  }

  @Override
  public boolean canEncode(Object value) {
    return false;
  }

  @Override
  public boolean canEncodeNull(Class<?> type) {
    return false;
  }

  @Override
  public Geometry decode(
      final ByteBuf buffer,
      final int dataType,
      final Format format,
      final Class<? extends Geometry> type
  ) {
    if (buffer == null) {
      return null;
    }

    final ByteGetter byteGetter = ByteGetter.forFormat(format, buffer);
    final byte bom = byteGetter.readByte();
    final ValueGetter valueGetter = ValueGetter.forBom(bom, byteGetter);

    return BinaryParser.parseGeometry(valueGetter);
  }

  @Override
  public Parameter encode(Object value) {
    return null;
  }

  @Override
  public Parameter encodeNull() {
    return null;
  }

  @Override
  public Class<?> type() {
    return GeometryTypes.class;
  }
}
