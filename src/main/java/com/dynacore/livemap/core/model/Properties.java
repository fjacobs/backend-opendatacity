package com.dynacore.livemap.core.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter @Setter
public abstract class Properties {
    @JsonProperty("name")
    protected String name;
    @JsonProperty("pubDate")
    protected LocalDateTime pubDate;
    @JsonProperty("retrievedFromThirdParty")
    protected LocalDateTime retrievedFromThirdParty;
    @JsonProperty("type")
    protected String type;

    public void setTimeOfRetrievalNow() {
        retrievedFromThirdParty = LocalDateTime.now();
    }
    public void setTimeOfRetrieval(LocalDateTime dateTime) {
        retrievedFromThirdParty = dateTime;
    }
}
