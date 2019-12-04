package com.dynacore.livemap.core.http.handlers;

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

@ChannelHandler.Sharable
public class EtagOutboundHandler extends ChannelOutboundHandlerAdapter {

    private final Logger log = LoggerFactory.getLogger(EtagOutboundHandler.class);

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        log.info("Send message");

        HttpMessage m = (HttpMessage) msg;
        log.info("Read Etag from channel ---> "+ ctx.channel().attr(ChannelAttrKey.ETAG).get() + " in channel " + ctx.channel().id());
        log.info("Read Last-Modified from channel ---> "+ ctx.channel().attr(ChannelAttrKey.LAST_MODIFIED).get() + " in channel " + ctx.channel().id());

        Optional<String>  etag =Optional.ofNullable(ctx.channel().attr(ChannelAttrKey.ETAG).get());
        Optional<String>  lastModified =Optional.ofNullable(ctx.channel().attr(ChannelAttrKey.LAST_MODIFIED).get());

        /* Don't set Etag if gzip is used.
            Use Last-Modified instead. (Last modified is ignored when both are set)
         */

        if(etag.isPresent()) {
            if( ! etag.get().contains("gzip")) {
                m.headers().set(HttpHeaderNames.IF_NONE_MATCH, etag.get());
            }
        } else {
            log.info("Did not find ETAG Attribute in channel context.");
        }
        if(lastModified.isPresent()) {
            m.headers().set(HttpHeaderNames.IF_MODIFIED_SINCE, lastModified.get().stripTrailing());
        } else {
            log.info("Did not find IF_MODIFIED_SINCE Attribute in channel context");
        }

        m.headers().add(HttpHeaders.ACCEPT_ENCODING, "gzip, deflate");

        log.info("Sending message with headers: ");
         m.headers().forEach(header -> log.info(header.getKey() + " value:" + header.getValue()));
        super.write(ctx, msg, promise);
    }
}
