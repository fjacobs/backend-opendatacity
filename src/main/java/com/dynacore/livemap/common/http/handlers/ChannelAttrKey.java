package com.dynacore.livemap.common.http.handlers;

import io.netty.util.AttributeKey;
import org.springframework.http.HttpHeaders;

public final class ChannelAttrKey {
    public static final AttributeKey<String> ETAG =
            AttributeKey.newInstance(HttpHeaders.ETAG);
}
