package com.dynacore.livemap.guidancesign.domain;

import com.dynacore.livemap.core.repository.TrafficEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;

import javax.persistence.Table;
import javax.persistence.Transient;
import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;


@Table(name = "GUIDANCE_SIGN")
@NoArgsConstructor
@Getter
@Setter
public class GuidanceSignEntity extends TrafficEntity {

  @Transient Logger logger = LoggerFactory.getLogger(GuidanceSignEntity.class);

  private boolean removed;
  private String state;

  // The time this system retrieved the data from the external provider
  private OffsetDateTime ourCreationDate;
  private Set<InnerDisplayEntity> innerDisplays;

  public Flux<InnerDisplayEntity> getInnerDisplays() {
    return Flux.fromIterable(innerDisplays);
  }

  public GuidanceSignEntity (GuidanceSignFeature feature) {
    id = feature.getId();
    name = feature.getName();
    pubDate = feature.getPubDate();
    removed = feature.getRemoved();
    state = feature.getState();
    ourCreationDate = feature.getOurCreationDate();
    innerDisplays =
            feature.getInnerDisplays().stream()
                    .map(
                            inner ->
                                    new InnerDisplayEntity.Builder()
                                            .innerDisplayId(inner.getId())
                                            .outputDescription(inner.getOutputDescription())
                                            .output(inner.getOutput())
                                            .type(inner.getType())
                                            .description(inner.getDescription())
                                            .guidanceSignEntity(this)
                                            .build())
                    .collect(Collectors.toSet());

//    Stream.of(id, name, removed, pubDate, state, innerDisplays)
//            .filter(Objects::isNull)
//            .findAny()
//            .ifPresent(
//                    nullMember -> {
//                      throw new IllegalStateException("Error: Could not initialize:  " + id);
//                    });
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof GuidanceSignEntity)) return false;
    GuidanceSignEntity that = (GuidanceSignEntity) o;
    return id.equals(that.id)
            && name.equals(that.name)
            && pubDate.equals(that.pubDate)
            && state.equals(that.state)
            && innerDisplays.equals(that.innerDisplays);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, name, pubDate, state, innerDisplays);
  }
}
