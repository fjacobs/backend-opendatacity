package com.dynacore.livemap.core.repository;

import java.time.OffsetDateTime;

public interface TrafficEntity {
    String getId();
    String getName();
    OffsetDateTime getPubDate();
    OffsetDateTime getOurRetrieval();

}
