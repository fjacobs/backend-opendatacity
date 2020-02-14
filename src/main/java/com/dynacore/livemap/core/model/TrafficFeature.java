package com.dynacore.livemap.core.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;
import java.time.OffsetDateTime;

public interface TrafficFeature {
    @JsonIgnore
    String ID = "Id";
    @JsonIgnore
    String NAME = "Name";
    @JsonIgnore
    String OUR_CREATION_DATE = "ourRetrieval";
    @JsonIgnore
    String THEIR_RETRIEVAL = "pubDate";
    @JsonIgnore
    String SAME_SINCE = "sameSince";

    String getId();
    void setId(String Id);

    void setName(String name);

    String getName();

    OffsetDateTime getPubDate();

    void setPubDate(OffsetDateTime pubDate);

    OffsetDateTime getSameSince();

    void setSameSince(OffsetDateTime date);

    OffsetDateTime getOurRetrieval();

    void setOurRetrieval(OffsetDateTime ourRetrieval);

}
