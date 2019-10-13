package com.dynacore.livemap.common.http.handlers;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.handler.codec.http.LastHttpContent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;

@ChannelHandler.Sharable
public class EtagInboundHandler extends ChannelInboundHandlerAdapter{
    private final Logger log = LoggerFactory.getLogger(EtagInboundHandler.class);
    public EtagInboundHandler() {
        log.info("EtagInboundHandler Created ************************************");
    }

    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        log.info("Enter EtagInboundHandler::channelRead");

        if (msg instanceof HttpResponse) {
            HttpResponse response = (HttpResponse) msg;
            log.trace("Receive: " + response.status());

            if (!response.headers().isEmpty()) {
                ctx.channel().attr(ChannelAttrKey.ETAG).set(response.headers().get(HttpHeaders.ETAG));
                log.debug( "Inserted key in attr: " + ctx.channel().attr(ChannelAttrKey.ETAG).get() + " in channel: " + ctx.channel().id());
            }

            if (HttpUtil.isTransferEncodingChunked(response)) {
  //             System.err.println("CHUNKED CONTENT {");
            } else {
  //              System.err.println("CONTENT {");
            }
        }
        if (msg instanceof HttpContent) {
            HttpContent content = (HttpContent) msg;
          //  System.err.print(content.content().toString(CharsetUtil.UTF_8));
            System.err.flush();

            if (content instanceof LastHttpContent) {
                 //System.err.println("} END OF CONTENT");
                ctx.close();
            }
        }
        ctx.fireChannelRead(msg);
    }

}

