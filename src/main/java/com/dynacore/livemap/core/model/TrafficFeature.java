package com.dynacore.livemap.core.model;

import com.dynacore.livemap.traveltime.repo.TravelTimeEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeName;
import org.geojson.Feature;

import java.time.OffsetDateTime;

@JsonTypeName("Feature")
public class TrafficFeature extends Feature {

  @JsonIgnore public static final String ID = "Id";
  @JsonIgnore public static final String NAME = "Name";
  @JsonIgnore public static final String OUR_RETRIEVAL = "retrievedFromThirdParty";
  @JsonIgnore public static final String THEIR_RETRIEVAL = "pubDate";
  @JsonIgnore public static final String SAME_SINCE = "sameSince";

  public TrafficFeature(){}

  public TrafficFeature(TravelTimeEntity entity) {
    setId(entity.getId());
    setName(entity.getName());
    setPubDate(entity.getPubDate());
  }

  public TrafficFeature(Feature feature) {
    setId(feature.getId());
    setName(feature.getProperty(NAME));
    setPubDate(feature.getProperty(THEIR_RETRIEVAL));
    setGeometry(feature.getGeometry());
  }

  public void setName(String name) {
    getProperties().put(NAME, name);
  }

  public OffsetDateTime getPubDate() {
    return (OffsetDateTime) getProperties().get(THEIR_RETRIEVAL);
  }

  public void setPubDate(OffsetDateTime pubDate) {
    getProperties().put(THEIR_RETRIEVAL, pubDate);
  }

  public OffsetDateTime getSameSince() {
    return (OffsetDateTime) getProperties().get(SAME_SINCE);
  }

  public void setSameSince(OffsetDateTime date) {
    getProperties().put(SAME_SINCE, date);
  }

  public OffsetDateTime getOurRetrieval() {
    return (OffsetDateTime) getProperties().get(OUR_RETRIEVAL);
  }

  public void setOurRetrieval(OffsetDateTime ourRetrieval) {
    getProperties().put(OUR_RETRIEVAL, ourRetrieval);
  }
}
