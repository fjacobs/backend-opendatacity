package com.dynacore.livemap.guidancesign.domain;

import com.dynacore.livemap.core.repository.TrafficEntityImpl;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Table;
import java.util.Objects;
import java.util.stream.Stream;

@ToString
@NoArgsConstructor
@Getter
@Setter
@Table(name = "guidance_sign_entity")
public class GuidanceSignEntity extends TrafficEntityImpl {

  private Boolean removed;
  private String state;
  private Integer pkey;

  public GuidanceSignEntity(GuidanceSignFeatureImpl feature) {
    id = feature.getId();
    name = feature.getName();
    pubDate = feature.getPubDate();
    removed = feature.getRemoved();
    state = feature.getState();
    ourRetrieval = feature.getOurRetrieval();
    Stream.of(id, name, removed, pubDate, state, ourRetrieval)
        .filter(Objects::isNull)
        .findAny()
        .ifPresent(
            nullMember -> {
              throw new IllegalStateException("Error: Could not initialize:  " + id);
            });
  }
}
