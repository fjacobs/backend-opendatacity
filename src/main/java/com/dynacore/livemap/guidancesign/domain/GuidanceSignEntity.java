package com.dynacore.livemap.guidancesign.domain;

import com.dynacore.livemap.core.repository.TrafficEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Table;
import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.stream.Stream;

@ToString
@Table(name = "guidance_sign")
@NoArgsConstructor
@Getter
@Setter
public class GuidanceSignEntity extends TrafficEntity {

  private Boolean removed;
  private String state;
  private Integer pkey;

  // The time this system retrieved the data from the external provider

  public GuidanceSignEntity (GuidanceSignFeature feature) {
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
