package com.dynacore.livemap.configuration;

import com.dynacore.livemap.core.http.handlers.EtagInboundHandler;
import com.dynacore.livemap.core.http.handlers.EtagOutboundHandler;
import com.dynacore.livemap.core.http.observer.ETagObserver;
import io.netty.handler.codec.http.HttpContentDecompressor;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpResponseStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.netty.ByteBufMono;
import reactor.netty.http.client.HttpClient;
import reactor.netty.http.client.HttpClientResponse;


@Component("httpClientFactory")
public class HttpClientFactoryConfig {

    private final String URL = "http://web.redant.net/~amsterdam/ndw/data/reistijdenAmsterdam.geojson";
    private final Logger log = LoggerFactory.getLogger(HttpClientFactoryConfig.class);

    public enum ServerCapability {
        DEFAULT,
        ETAG,
        LAST_MODIFIED,
        HTTP2  // HTTP2 is not yet supported by Reactor-Netty 0.9........
    }

    public HttpClientFactoryConfig() { }

    //Sends HTTP Head and determines server capabilities based on response
    public HttpClient autoConfigHttpClient(String URL) { //Todo: move inside ChannelHandlers and dynamically switch on runtime...
        ServerCapability capability = getServerCapability(URL);
        log.info("Found capability: " + capability);
        HttpClient client = null;
        switch(capability) {
            case DEFAULT: client = defaultClient(URL);  break;
            case ETAG: client = etagClient(URL); break; // etagClient(URL);  break;
            default:
                throw new IllegalStateException("Unexpected value: " + capability);
        }
        return client;
    }

    //Sends HTTP Head and determine server capabilities based on response
    public ServerCapability getServerCapability(String URL) { //Todo: move inside ChannelHandlers and dynamically switch on runtime...
        log.debug("enter getServerCapability");
        return HttpClient.create()
                .baseUrl(URL)
                .observe((conn, newState)-> log.info(newState.toString()))
                .port(80)
                .head()
                .uri("http://web.redant.net/")
                .responseSingle((HttpClientResponse metaResp, ByteBufMono ignoredContents) -> {
                    ServerCapability capability = null;
                    if (metaResp.status() == HttpResponseStatus.OK) {
                        //Configure handler pipeline
                        if (metaResp.responseHeaders().contains(HttpHeaderNames.ETAG)) {
                            capability = ServerCapability.ETAG;
                            log.debug("Etag detected....");
                        } else {
                            log.debug("No capabilities detected....");
                        }
                    } else {
                        log.error("Error server responded: " + metaResp.status());
                    }
                    return Mono.just(capability);
                })
                .block();
    }

    public HttpClient defaultClient(String URL) {
        return HttpClient.create()
                .baseUrl(URL)
                .port(80);
    }

    public HttpClient etagClient(String URL) {
        EtagOutboundHandler etagOutboundHandler = new EtagOutboundHandler();
        EtagInboundHandler  etagInboundHandler = new EtagInboundHandler();

        return HttpClient.create()
                .baseUrl(URL)
                .port(80)
                .observe(new ETagObserver())
                .doOnRequest((req, conn) -> {
                    conn.addHandler("dynacore.right.outboundhandler", etagOutboundHandler);
                    conn.addHandlerLast("netty.left.decompressor",  new HttpContentDecompressor());
                    conn.addHandlerLast("netty.left.httpobjectaggregator", new HttpObjectAggregator(1048576));
                    conn.addHandlerLast("dynacore.left.inboundhandler", etagInboundHandler);
                })
                .doOnResponse((req, conn) -> {
                    conn.addHandler("dynacore.right.outboundhandler", etagOutboundHandler);
                    conn.addHandlerLast("netty.left.decompressor",  new HttpContentDecompressor());
                    conn.addHandlerLast("netty.left.httpobjectaggregator", new HttpObjectAggregator(1048576));
                    conn.addHandlerLast("dynacore.left.inboundhandler", etagInboundHandler);
                });

    }

}
