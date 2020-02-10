package com.dynacore.livemap.guidancesign.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

@Getter
@Setter
public class PropertiesImpl  {

  String state;

  String type;

  boolean removed;

  @JsonProperty("ParkingguidanceDisplay")
  List<InnerDisplayModel> innerDisplayModelList;

  public PropertiesImpl() {}

  private PropertiesImpl(Builder builder) {
    type = builder.type;
    state = builder.state;
    removed = builder.removed;
    innerDisplayModelList = builder.innerDisplayModelList;
  }

  static final class Builder {
    private String name;
    private String type;
    private String state;
    private boolean removed;
    private List<InnerDisplayModel> innerDisplayModelList;

    Builder() {}

    Builder name(String val) {
      name = val;
      return this;
    }

//    Builder pubDate(OffsetDateTime val) {
//      pubDate = val;
//      return this;
//    }

    Builder type(String val) {
      type = val;
      return this;
    }

    Builder state(String val) {
      state = val;
      return this;
    }

    Builder removed(boolean val) {
      removed = val;
      return this;
    }

    Builder innerDisplayModelList(List<InnerDisplayModel> val) {
      innerDisplayModelList = val;
      return this;
    }

    PropertiesImpl build() {
      Stream.of(name, removed, type, state, removed, innerDisplayModelList)
          .filter(Objects::isNull)
          .findAny()
          .ifPresent(
              nullMember -> {
                throw new IllegalStateException(
                    "Error: Could not initialize:  "
                        + name
                        + " "
                        + nullMember.getClass().getSimpleName()
                        + " was not initialized.");
              });
      return new PropertiesImpl(this);
    }
  }
}
