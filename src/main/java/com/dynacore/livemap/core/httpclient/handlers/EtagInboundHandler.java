package com.dynacore.livemap.core.httpclient.handlers;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;

/*
    Inbound handler capable of both reading the Last-Modified and ETag header.
    (Both headers can be used to identify a specific variant of a resource.)
    This handler must be used in combination with the EtagOutboundHandler.
 */
@ChannelHandler.Sharable
public class EtagInboundHandler extends SimpleChannelInboundHandler<HttpObject> {
    private final Logger log = LoggerFactory.getLogger(EtagInboundHandler.class);

    public void channelRead0(ChannelHandlerContext ctx, HttpObject msg) {

        if (msg instanceof HttpResponse) {
            HttpResponse response = (HttpResponse) msg;
            log.info("Received HTTP response status: " + response.status());

            if (!response.headers().isEmpty()) {
                ctx.channel().attr(ChannelAttrKey.ETAG).set(response.headers().get(HttpHeaders.ETAG));
                log.debug("Inserted Etag: " + ctx.channel().attr(ChannelAttrKey.ETAG).get() + " in channel: " + ctx.channel().id());
                ctx.channel().attr(ChannelAttrKey.LAST_MODIFIED).set(response.headers().get(HttpHeaders.LAST_MODIFIED));
                log.debug("Inserted Last-Modified: " + ctx.channel().attr(ChannelAttrKey.LAST_MODIFIED).get() + " in channel: " + ctx.channel().id());
            }
        }
        if (msg instanceof HttpContent) {
            HttpContent content = (HttpContent) msg;
            System.err.flush();

            if (content instanceof LastHttpContent) {
                ctx.close();
            }
        }
        ctx.fireChannelRead(msg);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

}

