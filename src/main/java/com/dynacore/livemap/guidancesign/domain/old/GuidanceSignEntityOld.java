package com.dynacore.livemap.guidancesign.domain.old;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.annotation.Immutable;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Entity
@Immutable
@Table(name = "GUIDANCE_SIGN")
@NoArgsConstructor
@Getter
@Setter
public class GuidanceSignEntityOld implements Serializable {

  @Transient Logger logger = LoggerFactory.getLogger(GuidanceSignEntityOld.class);

  @Id
  @Column(name = "ID", nullable = false, updatable = false)
  private UUID guidanceSignId;

  private String name; // Flatted member from domain.properties

  @Id
  @Column(name = "PUB_DATE")
  private LocalDateTime pubDate;

  private boolean removed;
  private String state;

  // The time this system retrieved the data from the external provider
  private LocalDateTime retrievedFromThirdParty;

  @OneToMany(mappedBy = "parentRef", fetch = FetchType.LAZY)
  private Set<InnerDisplayEntityOld> innerDisplays;

  public GuidanceSignEntityOld(GuidanceSignModel model) {
    guidanceSignId = model.getId();
    name = model.getName();
    pubDate = model.getPubDate();
    removed = model.getRemoved();
    state = model.getState();
    retrievedFromThirdParty = model.getProperties().getRetrievedFromThirdParty();
    innerDisplays =
        model.getProperties().getInnerDisplayModelList().stream()
            .map(
                inner ->
                    new InnerDisplayEntityOld.Builder()
                        .innerDisplayId(inner.getId())
                        .outputDescription(inner.getOutputDescription())
                        .output(inner.getOutput())
                        .type(inner.getType())
                        .description(inner.getDescription())
                        .guidanceSignEntity(this)
                        .build())
            .collect(Collectors.toSet());

    Stream.of(guidanceSignId, name, removed, pubDate, state, innerDisplays)
        .filter(Objects::isNull)
        .findAny()
        .ifPresent(
            nullMember -> {
              throw new IllegalStateException("Error: Could not initialize:  " + guidanceSignId);
            });
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof GuidanceSignEntityOld)) return false;
    GuidanceSignEntityOld that = (GuidanceSignEntityOld) o;
    return guidanceSignId.equals(that.guidanceSignId)
        && name.equals(that.name)
        && pubDate.equals(that.pubDate)
        && state.equals(that.state)
        && innerDisplays.equals(that.innerDisplays);
  }

  @Override
  public int hashCode() {
    return Objects.hash(guidanceSignId, name, pubDate, state, innerDisplays);
  }
}
