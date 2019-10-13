package com.dynacore.livemap.common.http.observer;

import com.dynacore.livemap.common.http.handlers.ChannelAttrKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.netty.Connection;
import reactor.netty.ConnectionObserver;
import reactor.netty.http.client.HttpClientState;
import reactor.util.context.Context;

public class ETagObserver implements ConnectionObserver {

    private final Logger log = LoggerFactory.getLogger(ETagObserver.class);
    private String eTag = ""; //Channel doesn't remember state in 0.9 so we remember ChannelHandler context here.
    private Connection connectionState;

    public ETagObserver() {
        log.info("EtagObserver Created ************************************");
    }


    @Override
    public void onStateChange(Connection connection, State newState) {
        //      log.info("Channel ID :  " + connection.channel().id());
        log.info("Connection stateChange:  " + newState);
        if (newState == HttpClientState.RESPONSE_RECEIVED) {
            eTag = connection.channel().attr(ChannelAttrKey.ETAG).get();
            log.info("Inside ETagObserver --> HttpClientState.RESPONSE_RECEIVED : Etag loaded from channel  " + eTag);
        }
        if (newState == HttpClientState.CONFIGURED) {
            connection.channel().attr(ChannelAttrKey.ETAG).setIfAbsent(eTag);
            log.info("Inside ETagObserver --> HttpClientState.CONFIGURED : Etag set in channel: " + eTag);
        }
    }

}
