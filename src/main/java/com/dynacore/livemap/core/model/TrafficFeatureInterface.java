package com.dynacore.livemap.core.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;
import java.time.OffsetDateTime;

public interface TrafficFeatureInterface extends Serializable {
    @JsonIgnore
    String ID = "Id";
    @JsonIgnore
    String NAME = "Name";
    @JsonIgnore
    String OUR_RETRIEVAL = "retrievedFromThirdParty";
    @JsonIgnore
    String THEIR_RETRIEVAL = "pubDate";
    @JsonIgnore
    String SAME_SINCE = "sameSince";

    void setName(String name);

    String getName();

    OffsetDateTime getPubDate();

    void setPubDate(OffsetDateTime pubDate);

    OffsetDateTime getSameSince();

    void setSameSince(OffsetDateTime date);

    OffsetDateTime getOurRetrieval();

    void setOurRetrieval(OffsetDateTime ourRetrieval);
}
