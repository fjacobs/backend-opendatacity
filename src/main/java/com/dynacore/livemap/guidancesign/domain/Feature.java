package com.dynacore.livemap.guidancesign.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public abstract class Feature {

  @JsonProperty("Id")
  protected UUID id;

  @JsonProperty("type")
  protected String type;

  @JsonProperty("geometry")
  protected Geometry geometry;

  @JsonIgnore protected Properties properties;
}
