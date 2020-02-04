package com.dynacore.livemap.parking.repo;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.OffsetDateTime;
import java.util.Objects;

@Table
@Getter
@Setter
@NoArgsConstructor
@ToString
public class ParkingEntity {

  private static final Logger log = LoggerFactory.getLogger(ParkingEntity.class);

  @Id private Integer pkey;

  private String id;
  private String name;

  private OffsetDateTime pubDate;
  private OffsetDateTime retrievedFromThirdParty;
  private String type;

  private Integer length;
  private Integer travel_time;
  private Integer velocity;

  public ParkingEntity(
      Integer pkey,
      String id,
      String name,
      OffsetDateTime pubDate,
      OffsetDateTime retrievedFromThirdParty,
      String type,
      Integer length,
      Integer travel_time,
      Integer velocity) {
    this.pkey = pkey;
    this.id = id;
    this.name = name;
    this.pubDate = pubDate;
    this.retrievedFromThirdParty = retrievedFromThirdParty;
    this.type = type;
    this.length = length;
    this.travel_time = travel_time;
    this.velocity = velocity;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof ParkingEntity)) return false;
    ParkingEntity that = (ParkingEntity) o;
    return Objects.equals(length, that.length)
        && Objects.equals(travel_time, that.travel_time)
        && Objects.equals(velocity, that.velocity);
  }

  @Override
  public int hashCode() {
    return Objects.hash(length, travel_time, velocity);
  }
}
