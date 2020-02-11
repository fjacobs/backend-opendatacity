package com.dynacore.livemap.core.repository;

import java.time.OffsetDateTime;

public interface TrafficEntityInterface {
    String getId();
    String getName();
    OffsetDateTime getPubDate();
    OffsetDateTime getOurRetrieval();

}
