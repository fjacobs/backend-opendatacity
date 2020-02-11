package com.dynacore.livemap.guidancesign.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;

@Getter
@Setter
public abstract class Properties {
  @JsonProperty("name")
  protected String name;

  @JsonProperty("pubDate")
  protected OffsetDateTime pubDate;

  @JsonProperty("ourRetrieval")
  protected OffsetDateTime ourRetrieval;

  @JsonProperty("type")
  protected String type;

  public void setTimeOfRetrievalNow() {
    ourRetrieval = OffsetDateTime.now();
  }

  public void setTimeOfRetrieval(OffsetDateTime dateTime) {
    ourRetrieval = dateTime;
  }
}
