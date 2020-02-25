package com.dynacore.livemap.traveltime.domain;

import com.dynacore.livemap.core.model.TrafficMapDTO;
import com.dynacore.livemap.traveltime.repo.TravelTimeEntityImpl;
import lombok.NoArgsConstructor;
import org.geojson.Geometry;
import org.geojson.LineString;

@NoArgsConstructor
public class TravelTimeMapDTO extends TrafficMapDTO {

  public TravelTimeMapDTO(TravelTimeEntityImpl entity) {
    setId(entity.getId());
    setPubDate(entity.getPubDate());

    if(entity.getVelocity() == null) { setVelocity(-1);
    } else{
      setVelocity(entity.getVelocity());
    }
  }

  public void setType(String type) {
    put(TravelTimeFeatureImpl.TYPE, type);
  }

  public Integer getTravelTime() {
    return (Integer) get(TravelTimeFeatureImpl.TRAVEL_TIME);
  }

  public void setTravelTime(Integer travelTime) {
    put(TravelTimeFeatureImpl.TRAVEL_TIME, travelTime);
  }

  public Integer getLength() {
    return (Integer) get(TravelTimeFeatureImpl.LENGTH);
  }

  public void setLength(Integer length) {
    put(TravelTimeFeatureImpl.LENGTH, length);
  }

  public Integer getVelocity() {
    return (Integer) get(TravelTimeFeatureImpl.VELOCITY);
  }

  public void setVelocity(Integer velocity) {
    put(TravelTimeFeatureImpl.VELOCITY, velocity);
  }

  public String getType() {
    return (String) get(TravelTimeFeatureImpl.TYPE);
  }

  public void setGeometry(Geometry<LineString> geometry) {

  }
}
