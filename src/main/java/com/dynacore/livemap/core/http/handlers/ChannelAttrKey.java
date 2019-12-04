package com.dynacore.livemap.core.http.handlers;

import io.netty.util.AttributeKey;
import org.springframework.http.HttpHeaders;

public final class ChannelAttrKey {
    public static final AttributeKey<String> ETAG =
            AttributeKey.newInstance(HttpHeaders.ETAG);

    public static final AttributeKey<String> LAST_MODIFIED =
            AttributeKey.newInstance(HttpHeaders.LAST_MODIFIED);
}
