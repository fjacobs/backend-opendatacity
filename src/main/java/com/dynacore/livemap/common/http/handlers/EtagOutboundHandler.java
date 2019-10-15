package com.dynacore.livemap.common.http.handlers;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Optional;

@ChannelHandler.Sharable
public class EtagOutboundHandler extends ChannelOutboundHandlerAdapter {

    private final Logger log = LoggerFactory.getLogger(EtagOutboundHandler.class);

    public EtagOutboundHandler() {
        log.info("EtagOutboundHandler Created ************************************");
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        log.info("Send message");

        HttpMessage m = (HttpMessage) msg;
        log.info("Is ETAG THERE---> "+ ctx.channel().attr(ChannelAttrKey.ETAG).get() + " in channel " + ctx.channel().id());

        Optional.ofNullable(ctx.channel().attr(ChannelAttrKey.ETAG).get())
                .ifPresentOrElse(eTag -> m.headers().set(HttpHeaderNames.IF_NONE_MATCH, eTag),
                        () -> log.info("Did not set key in headers because there is no ETAG Attribute in context....."));

        log.info("Sending message with headers: ");
        m.headers().forEach(header -> log.info(header.getKey() + " value:" + header.getValue()));

        super.write(ctx, msg, promise);
    }
}
