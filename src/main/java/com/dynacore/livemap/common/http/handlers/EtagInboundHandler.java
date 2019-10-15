package com.dynacore.livemap.common.http.handlers;

import com.dynacore.livemap.common.model.FeatureCollection;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.DecoderResult;
import io.netty.handler.codec.http.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;

@ChannelHandler.Sharable
public class EtagInboundHandler extends SimpleChannelInboundHandler<HttpObject> {
    private final Logger log = LoggerFactory.getLogger(EtagInboundHandler.class);
    private final StringBuilder buf = new StringBuilder();
    public EtagInboundHandler() {
        log.info("EtagInboundHandler Created ************************************");
    }

    public void channelRead0(ChannelHandlerContext ctx, HttpObject msg) {
        log.info("Enter EtagInboundHandler::channelRead");

        if (msg instanceof HttpResponse) {
            HttpResponse response = (HttpResponse) msg;
            log.trace("Receive: " + response.status());

            if (!response.headers().isEmpty()) {
                ctx.channel().attr(ChannelAttrKey.ETAG).set(response.headers().get(HttpHeaders.ETAG));
                log.debug("Inserted key in attr: " + ctx.channel().attr(ChannelAttrKey.ETAG).get() + " in channel: " + ctx.channel().id());
//                if(response.status() == HttpResponseStatus.NOT_MODIFIED) {
//                    buf.setLength(0);
//                    buf.append(new FeatureCollection<>().toString());
//                    appendDecoderResult(buf, response);
//                    buf.append("\r\n");
//                }
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















     private static void appendDecoderResult(StringBuilder buf, HttpObject o) {
                 DecoderResult result = o.getDecoderResult();
                 if (result.isSuccess()) {
                         return;
                     }

                 buf.append(".. WITH DECODER FAILURE: ");
                 buf.append(result.cause());
                 buf.append("\r\n");
             }



    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

}

