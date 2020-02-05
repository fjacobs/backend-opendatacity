package com.dynacore.livemap.traveltime.domain;

import com.dynacore.livemap.core.geojson.TrafficFeature;
import com.dynacore.livemap.traveltime.repo.TravelTimeEntity;

// Wrapper class for GeoJson Feature
// See visitor package for methods.
public class TravelTimeFeature extends TrafficFeature {


  public static final String TYPE = "Type";
  public static final String TRAVEL_TIME = "Traveltime";
  public static final String LENGTH = "Length";
  public static final String VELOCITY = "Velocity";

  public TravelTimeFeature(){}

  public TravelTimeFeature(TravelTimeEntity entity) {
    setId(entity.getId());
    setName(entity.getName());
    setPubDate(entity.getPubDate());
    setOurRetrieval(entity.getRetrievedFromThirdParty());
    setType(entity.getType());
    setLength(entity.getLength());
    setTravelTime(entity.getTravel_time());
    setVelocity(entity.getVelocity());
  }

  public String getType() {
    return getProperty(TYPE);
  }

  public Integer getLength() {
    return getProperty(LENGTH);
  }

  public Integer getTravelTime() {
    return getProperty(TRAVEL_TIME);
  }

  public Integer getVelocity() {
    return getProperty(VELOCITY);
  }



  public void setType(String type) {
    getProperties().put(TYPE, type);
  }

  public void setLength(Integer length) {
    getProperties().put(LENGTH, length);
  }

  public void setTravelTime(Integer travelTime) {
    getProperties().put(TRAVEL_TIME, travelTime);
  }

  public void setVelocity(Integer velocity) {
    getProperties().put(VELOCITY, velocity);
  }
}
