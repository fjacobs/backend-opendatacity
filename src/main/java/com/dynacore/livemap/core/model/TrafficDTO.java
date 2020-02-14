package com.dynacore.livemap.core.model;

import java.time.OffsetDateTime;

public interface TrafficDTO {
    void setId(String id);
    String getId();
    OffsetDateTime getPubDate();
    void setPubDate(OffsetDateTime pubDate);
}
