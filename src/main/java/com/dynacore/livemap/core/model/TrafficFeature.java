package com.dynacore.livemap.core.model;

import com.dynacore.livemap.core.repository.TrafficEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeName;
import org.geojson.Feature;

import java.time.OffsetDateTime;

@JsonTypeName("Feature")
public class TrafficFeature extends Feature implements TrafficFeatureInterface {

  public TrafficFeature() {}

  public TrafficFeature(TrafficEntity entity) {
    setId(entity.getId());
    setName(entity.getName());
    setPubDate(entity.getPubDate());
  }
  //Different suppliers use different keys for it's pubdate:
  public TrafficFeature(Feature feature, String pubDateKey) {
    setId(feature.getId());
    setOurRetrieval(OffsetDateTime.parse(feature.getProperty(OUR_RETRIEVAL)));
    setPubDate(OffsetDateTime.parse(feature.getProperty(pubDateKey)));
    setName(feature.getProperty(NAME));
    setGeometry(feature.getGeometry());
  }

  @Override
  public void setName(String name) {
    getProperties().put(NAME, name);
  }

  @Override
  public String getName() {
    return (String) getProperty(NAME);
  }

  @Override
  public OffsetDateTime getPubDate() {
    return (OffsetDateTime) getProperties().get(THEIR_RETRIEVAL);
  }

  @Override
  public void setPubDate(OffsetDateTime pubDate) {
    getProperties().put(THEIR_RETRIEVAL, pubDate);
  }

  @Override
  public OffsetDateTime getSameSince() {
    return (OffsetDateTime) getProperties().get(SAME_SINCE);
  }

  @Override
  public void setSameSince(OffsetDateTime date) {
    getProperties().put(SAME_SINCE, date);
  }

  @Override
  public OffsetDateTime getOurRetrieval() {
    return (OffsetDateTime) getProperties().get(OUR_RETRIEVAL);
  }

  @Override
  public void setOurRetrieval(OffsetDateTime ourRetrieval) {
    getProperties().put(OUR_RETRIEVAL, ourRetrieval);
  }

}
