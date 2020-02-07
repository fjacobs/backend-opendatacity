package com.dynacore.livemap.core.repository;

import com.dynacore.livemap.traveltime.repo.TravelTimeEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.OffsetDateTime;

@ToString
@Getter @Setter
@NoArgsConstructor
public abstract class TrafficEntity {

  @Id protected Integer pkey;

  protected String id;
  protected String name;

  protected OffsetDateTime pubDate;
  protected OffsetDateTime retrievedFromThirdParty;

  public TrafficEntity(
      Integer pkey,
      String id,
      String name,
      OffsetDateTime pubDate,
      OffsetDateTime retrievedFromThirdParty) {
    this.pkey = pkey;
    this.id = id;
    this.name = name;
    this.pubDate = pubDate;
    this.retrievedFromThirdParty = retrievedFromThirdParty;
  }
}
