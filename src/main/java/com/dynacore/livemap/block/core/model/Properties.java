package com.dynacore.livemap.block.core.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public abstract class Properties {
  @JsonProperty("name")
  protected String name;

  @JsonProperty("pubDate")
  protected LocalDateTime pubDate;

  @JsonProperty("ourRetrieval")
  protected LocalDateTime ourRetrieval;

  @JsonProperty("type")
  protected String type;

  public void setTimeOfRetrievalNow() {
    ourRetrieval = LocalDateTime.now();
  }

  public void setTimeOfRetrieval(LocalDateTime dateTime) {
    ourRetrieval = dateTime;
  }
}
