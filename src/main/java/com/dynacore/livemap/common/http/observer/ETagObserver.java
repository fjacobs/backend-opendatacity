package com.dynacore.livemap.common.http.observer;

import com.dynacore.livemap.common.http.handlers.ChannelAttrKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.netty.Connection;
import reactor.netty.ConnectionObserver;
import reactor.netty.http.client.HttpClientState;

public class ETagObserver implements ConnectionObserver {

    private final Logger log = LoggerFactory.getLogger(ETagObserver.class);
    private String eTag = "";           //Channel doesn't remember state in 0.9 for now remember ChannelHandler context here.
    private String lastModified = "";

    public ETagObserver() {
        log.trace("EtagObserver created ");
    }

    @Override
    public void onStateChange(Connection connection, State newState) {
        log.info("Connection stateChange:  " + newState);
        if (newState == HttpClientState.RESPONSE_RECEIVED) {
            eTag = connection.channel().attr(ChannelAttrKey.ETAG).get();
            lastModified = connection.channel().attr(ChannelAttrKey.LAST_MODIFIED).get();
        }
        if (newState == HttpClientState.CONFIGURED) {
            connection.channel().attr(ChannelAttrKey.ETAG).setIfAbsent(eTag);
            eTag = connection.channel().attr(ChannelAttrKey.ETAG).get();

            connection.channel().attr(ChannelAttrKey.LAST_MODIFIED).setIfAbsent(lastModified);
            lastModified = connection.channel().attr(ChannelAttrKey.LAST_MODIFIED).get();
        }
    }

}
