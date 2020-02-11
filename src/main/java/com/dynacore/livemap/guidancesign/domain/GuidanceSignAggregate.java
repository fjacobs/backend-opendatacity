package com.dynacore.livemap.guidancesign.domain;

import com.dynacore.livemap.core.repository.TrafficEntityInterface;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.OffsetDateTime;

@Data
@NoArgsConstructor
@EqualsAndHashCode(exclude = "pkey")
public class GuidanceSignAggregate implements TrafficEntityInterface {

  Integer pkey;
  private GuidanceSignEntity guidanceSignEntity;
  private Flux<InnerDisplayEntity> innerDisplayEntities = Flux.empty();

  public GuidanceSignAggregate(GuidanceSignFeature feature) {
    guidanceSignEntity = new GuidanceSignEntity(feature);
    innerDisplayEntities =
        Flux.fromIterable(feature.getInnerDisplays())
            .map(
                inner ->
                    new InnerDisplayEntity.Builder()
                        .innerDisplayId(inner.getId())
                        .fk(pkey)
                        .outputDescription(inner.getOutputDescription())
                        .output(inner.getOutput())
                        .type(inner.getType())
                        .description(inner.getDescription())
                        .build());
  }

  public GuidanceSignAggregate(GuidanceSignEntity entity) {
    guidanceSignEntity = entity;
  }

  public void setInnerDisplayEntities(Flux<InnerDisplayEntity> innerDisplayEntities) {
    this.innerDisplayEntities = innerDisplayEntities;
  }

  public Flux<InnerDisplayEntity> getInnerDisplayEntities() {
    return innerDisplayEntities;
  }

  public void setFk(Integer fk) {
    pkey = fk;
  }

  @Override
  public String getId() {
    return guidanceSignEntity.getId();
  }

  @Override
  public String getName() {
    return guidanceSignEntity.getName();
  }

  @Override
  public OffsetDateTime getPubDate() {
    return guidanceSignEntity.getPubDate();
  }

  @Override
  public OffsetDateTime getOurRetrieval() {
    return guidanceSignEntity.getOurRetrieval();
  }

  public Integer getPkey() {
    return pkey;
  }
}
