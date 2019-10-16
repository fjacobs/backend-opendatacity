package com.dynacore.livemap.common.http.handlers;

import com.dynacore.livemap.common.model.FeatureCollection;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;

@ChannelHandler.Sharable
public class EtagInboundHandler extends SimpleChannelInboundHandler<HttpObject> {
    private final Logger log = LoggerFactory.getLogger(EtagInboundHandler.class);

    public void channelRead0(ChannelHandlerContext ctx, HttpObject msg) {
        if (msg instanceof HttpResponse) {
            HttpResponse response = (HttpResponse) msg;
            log.trace("Receive: " + response.status());

            if (!response.headers().isEmpty()) {
                ctx.channel().attr(ChannelAttrKey.ETAG).set(response.headers().get(HttpHeaders.ETAG));
                log.debug("Inserted key in attr: " + ctx.channel().attr(ChannelAttrKey.ETAG).get() + " in channel: " + ctx.channel().id());
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

