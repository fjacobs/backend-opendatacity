package com.dynacore.livemap.traveltime.domain;

import com.dynacore.livemap.core.model.TrafficDTO;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@NoArgsConstructor
public class TravelTimeDTO extends TrafficDTO {

  public TravelTimeDTO(String id, OffsetDateTime pubDate, int velocity) {
    setId(id);
    setPubDate(pubDate);
    setVelocity(velocity);
  }

  public void setType(String type) {
    put(TravelTimeFeature.TYPE, type);
  }

  public Integer getTravelTime() {
    return (Integer) get(TravelTimeFeature.TRAVEL_TIME);
  }

  public void setTravelTime(Integer travelTime) {
    put(TravelTimeFeature.TRAVEL_TIME, travelTime);
  }

  public Integer getLength() {
    return (Integer) get(TravelTimeFeature.LENGTH);
  }

  public void setLength(Integer length) {
    put(TravelTimeFeature.LENGTH, length);
  }

  public Integer getVelocity() {
    return (Integer) get(TravelTimeFeature.VELOCITY);
  }

  public void setVelocity(Integer velocity) {
    put(TravelTimeFeature.VELOCITY, velocity);
  }

  public String getType() {
    return (String) get(TravelTimeFeature.TYPE);
  }
}
