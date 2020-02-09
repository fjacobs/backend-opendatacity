package com.dynacore.livemap.traveltime.repo;

import com.dynacore.livemap.core.repository.TrafficEntity;
import com.dynacore.livemap.traveltime.domain.TravelTimeFeature;
import lombok.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.relational.core.mapping.Table;

import java.time.OffsetDateTime;
import java.util.Objects;

@Table
@ToString
@Getter @Setter
public class TravelTimeEntity extends TrafficEntity {

  private static final Logger log = LoggerFactory.getLogger(TravelTimeEntity.class);

  private String type;
  private Integer length;
  private Integer travel_time;
  private Integer velocity;

  public TravelTimeEntity() { }

  public TravelTimeEntity(TravelTimeFeature feature) {

    super(null, feature.getId(), feature.getName(), feature.getPubDate(), feature.getOurCreationDate());
//    System.out.println("printing casted feature to tt:" + feature);
    setLength(feature.getLength());
    setTravel_time(feature.getTravelTime());
    setType(feature.getType());
    setVelocity(feature.getVelocity());
  }

  public TravelTimeEntity(
      Integer pkey,
      String id,
      String name,
      OffsetDateTime pubDate,
      OffsetDateTime retrievedFromThirdParty,
      String type,
      Integer length,
      Integer travel_time,
      Integer velocity) {
    super(pkey, id, name, pubDate, retrievedFromThirdParty);
    this.type = type;
    this.length = length;
    this.travel_time = travel_time;
    this.velocity = velocity;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof TravelTimeEntity)) return false;
    TravelTimeEntity that = (TravelTimeEntity) o;
    return Objects.equals(length, that.length)
        && Objects.equals(travel_time, that.travel_time)
        && Objects.equals(velocity, that.velocity);
  }

  @Override
  public int hashCode() {
    return Objects.hash(length, travel_time, velocity);
  }
}
