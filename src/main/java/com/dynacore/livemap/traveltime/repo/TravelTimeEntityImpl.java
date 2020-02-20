package com.dynacore.livemap.traveltime.repo;

import com.dynacore.livemap.core.repository.TrafficEntityImpl;
import com.dynacore.livemap.traveltime.domain.TravelTimeFeatureImpl;
import lombok.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.relational.core.mapping.Table;

import java.time.OffsetDateTime;
import java.util.Objects;

@Table("travel_time_entity")
@ToString
@Getter @Setter
@AllArgsConstructor
public class TravelTimeEntityImpl extends TrafficEntityImpl {

  private static final Logger log = LoggerFactory.getLogger(TravelTimeEntityImpl.class);

  private String type;
  private Integer length;
  private Integer travel_time;
  private Integer velocity;

  public TravelTimeEntityImpl(TravelTimeFeatureImpl feature) {

    super(null, feature.getId(), feature.getName(), feature.getPubDate(), feature.getOurRetrieval());
//    System.out.println("printing casted feature to tt:" + feature);
    setLength(feature.getLength());
    setTravel_time(feature.getTravelTime());
    setType(feature.getType());
    setVelocity(feature.getVelocity());
  }

  public TravelTimeEntityImpl(
      Integer pkey,
      String id,
      String name,
      OffsetDateTime pubDate,
      OffsetDateTime ourRetrieval,
      String type,
      Integer length,
      Integer travel_time,
      Integer velocity) {
    super(pkey, id, name, pubDate, ourRetrieval);
    this.type = type;
    this.length = length;
    this.travel_time = travel_time;
    this.velocity = velocity;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof TravelTimeEntityImpl)) return false;
    TravelTimeEntityImpl that = (TravelTimeEntityImpl) o;
    return Objects.equals(length, that.length)
        && Objects.equals(travel_time, that.travel_time)
        && Objects.equals(velocity, that.velocity);
  }

  @Override
  public int hashCode() {
    return Objects.hash(length, travel_time, velocity);
  }

}
