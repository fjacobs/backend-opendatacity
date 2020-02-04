package com.dynacore.livemap.core.httpclient.handlers;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;

import java.util.Optional;

/*
   This outbound handler is capable of asking the server to only send new content.
   It MUST be used in combination with the EtagInboundHandler

   It is capable of sending both the 'If-Modified-Since' and the 'If-None-Match' header, using data from the context
   channel that was assembled by the EtagInboundHandler.

   The 'If-Modified-Since' header uses the value from the inbound 'Last-Modified' header.
   The 'If-None-Match' uses the the value from the inbound 'ETag' header.

   In the particular scenario that the server uses gzip compression and both If-Modified-Since and If-None-Match,
   are sent by the server, then don't set Etag (If-None-Match) and only use the Last-Modified (If-Modified-Since) header.
   The reason is that the server will return 200 instead of 304 because the etag was calculated before compression
   changing it's value.
   see: https://stackoverflow.com/questions/29127144/cant-cache-resource-when-having-both-gzip-and-etag
*/
@ChannelHandler.Sharable
public class EtagOutboundHandler extends ChannelOutboundHandlerAdapter {

  private final Logger log = LoggerFactory.getLogger(EtagOutboundHandler.class);

  @Override
  public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise)
      throws Exception {

    HttpMessage m = (HttpMessage) msg;
    log.debug(
        "Assembling HTTP Request header: Read Etag: "
            + ctx.channel().attr(ChannelAttrKey.ETAG).get()
            + " from channel: '"
            + ctx.channel().id()
            + "'");
    log.debug(
        "Assembling HTTP Request header: Read 'Last-Modified' "
            + ctx.channel().attr(ChannelAttrKey.LAST_MODIFIED).get()
            + " from channel "
            + ctx.channel().id());

    Optional<String> etag = Optional.ofNullable(ctx.channel().attr(ChannelAttrKey.ETAG).get());
    Optional<String> lastModified =
        Optional.ofNullable(ctx.channel().attr(ChannelAttrKey.LAST_MODIFIED).get());

    if (etag.isPresent()) {
      log.debug("Found etag, checking if data was compressed...");
      if (etag.get().contains("gzip")) {
        log.debug("Data was compressed: don't include ETAG");
      } else {
        m.headers().set(HttpHeaderNames.IF_NONE_MATCH, etag.get());
        log.debug("Data was not compressed: include ETAG");
      }
    } else {
      log.debug("No ETAG found in channel.");
    }
    if (lastModified.isPresent()) {
      m.headers().set(HttpHeaderNames.IF_MODIFIED_SINCE, lastModified.get().stripTrailing());
    } else {
      log.debug("No 'IF_MODIFIED_SINCE' Attribute found in channel");
    }

    m.headers().add(HttpHeaders.ACCEPT_ENCODING, "gzip, deflate");

    log.info("Sending message with headers: ");
    m.headers().forEach(header -> log.info(header.getKey() + " value:" + header.getValue()));
    super.write(ctx, msg, promise);
  }
}
