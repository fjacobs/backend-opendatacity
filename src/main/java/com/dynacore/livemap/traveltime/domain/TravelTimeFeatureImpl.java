package com.dynacore.livemap.traveltime.domain;

import com.dynacore.livemap.core.model.TrafficFeatureImpl;
import com.dynacore.livemap.traveltime.repo.TravelTimeEntityImpl;
import org.geojson.Feature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.NoSuchElementException;

// Wrapper class for GeoJson Feature
public class TravelTimeFeatureImpl extends TrafficFeatureImpl {

  Logger log = LoggerFactory.getLogger(TravelTimeFeatureImpl.class);

  public static final String TYPE = "Type";
  public static final String TRAVEL_TIME = "Traveltime";
  public static final String LENGTH = "Length";
  public static final String VELOCITY = "Velocity";
  public static final String THEIR_PUB_DATE_KEY = "Timestamp"; // Different suppliers use different pubdates

  public TravelTimeFeatureImpl() {}

  // Internal Import
  public TravelTimeFeatureImpl(TravelTimeEntityImpl entity) {
    super(entity);
    setPubDate(entity.getPubDate());
    setOurRetrieval(entity.getOurRetrieval());
    setType(entity.getType());
    setLength(entity.getLength());
    setTravelTime(entity.getTravel_time());
    setVelocity(entity.getVelocity());
  }

  // External Import
  public TravelTimeFeatureImpl(Feature feature) {
    super(feature, THEIR_PUB_DATE_KEY);
    try {

//      if (!feature.getProperties().containsKey(TRAVEL_TIME))
//        feature.getProperties().put(TRAVEL_TIME, -1);
//      if (!feature.getProperties().containsKey(VELOCITY)) feature.getProperties().put(VELOCITY, -1);
//      OffsetDateTime retrieved = OffsetDateTime.now(ZoneOffset.UTC);
//      feature.getProperties().put(OUR_CREATION_DATE, retrieved.toString());
      if (feature.getProperties().containsKey(ID)) {
        feature.setId(
            (String)
                feature
                    .getProperties()
                    .get(
                        ID)); // See RFC 7946: If an ID is used then it SHOULD be included as a top
                              // level member
        feature.getProperties().remove(ID); // and not as a member of properties
      }
//      if (!feature.getProperties().containsKey(TYPE)) throw new NoSuchElementException(TYPE);
//      if (!feature.getProperties().containsKey(LENGTH)) throw new NoSuchElementException(LENGTH);
    } catch (NoSuchElementException error) {
      log.error(error.getMessage());
    }
  }

  public String getType() {
    return feature.getProperty(TYPE);
  }

  public Integer getLength() {
    return feature.getProperty(LENGTH);
  }

  public Integer getTravelTime() {
    return feature.getProperty(TRAVEL_TIME);
  }

  public Integer getVelocity() {
    return feature.getProperty(VELOCITY);
  }

  public void setType(String type) {
    feature.getProperties().put(TYPE, type);
  }

  public void setLength(Integer length) {
    feature.getProperties().put(LENGTH, length);
  }

  public void setTravelTime(Integer travelTime) {
    feature.getProperties().put(TRAVEL_TIME, travelTime);
  }

  public void setVelocity(Integer velocity) {
    feature.getProperties().put(VELOCITY, velocity);
  }


  @Override
  public String toString() {
    return "TravelTimeFeature{" +
            "feature=" + feature.toString() +
            '}';
  }

}
