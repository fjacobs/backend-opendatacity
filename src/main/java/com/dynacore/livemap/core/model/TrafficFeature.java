package com.dynacore.livemap.core.model;

import com.dynacore.livemap.core.repository.TrafficEntity;
import com.dynacore.livemap.core.repository.TrafficEntityInterface;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Builder;
import org.geojson.Feature;
import org.geojson.GeoJsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.OffsetDateTime;

//Typesafe wrapper for GeoJson Feature
@JsonTypeName("Feature")
public class TrafficFeature implements TrafficFeatureInterface {
  Logger log = LoggerFactory.getLogger(TrafficFeature.class);
  protected Feature feature;

  public TrafficFeature() {
    feature = new Feature();
  }

  public TrafficFeature(TrafficEntityInterface entity) {
    feature = new Feature();
    setId(entity.getId());
    setName(entity.getName());
    setPubDate(entity.getPubDate());
    setOurRetrieval(entity.getOurRetrieval());

  }
  // Different suppliers use different keys for the data publication timestamp:
  public TrafficFeature(Feature feature, String pubDateKey) {
    try {
      this.feature = feature;

      if (feature.getProperties().containsKey(pubDateKey)) {
        feature.getProperties().put(THEIR_RETRIEVAL, feature.getProperties().get(pubDateKey));// new key
        feature.getProperties().remove(pubDateKey);
      } else {
        log.warn("No pubdate key found during Feature import");
      }
    } catch (Exception error) {
      log.error("Error mapping feature to TrafficFeature" + error.getMessage());
      throw error;
    }
  }

  @Override
  public String getId() {
    return feature.getId();
  }

  @Override
  public void setId(String Id) {
    feature.setId(Id);
  }

  @Override
  public void setName(String name) {
    feature.getProperties().put(NAME, name);
  }

  @Override
  public String getName() {
    return (String) feature.getProperty(NAME);
  }

  @Override
  public OffsetDateTime getPubDate() {
    if (feature.getProperties().get(THEIR_RETRIEVAL) instanceof String) {
      return OffsetDateTime.parse((String) feature.getProperties().get(THEIR_RETRIEVAL));
    } else {
      return (OffsetDateTime) feature.getProperties().get(THEIR_RETRIEVAL);
    }
  }

  @Override
  public void setPubDate(OffsetDateTime pubDate) {
    feature.getProperties().put(THEIR_RETRIEVAL, pubDate);
  }

  @Override
  public OffsetDateTime getSameSince() {
    return (OffsetDateTime) feature.getProperties().get(SAME_SINCE);
  }

  @Override
  public void setSameSince(OffsetDateTime date) {
    feature.getProperties().put(SAME_SINCE, date);
  }

  @Override
  public OffsetDateTime getOurRetrieval() {
    if (feature.getProperties().get(OUR_CREATION_DATE) instanceof String) {
      return OffsetDateTime.parse((String) feature.getProperties().get(OUR_CREATION_DATE));
    } else {
      return (OffsetDateTime) feature.getProperties().get(OUR_CREATION_DATE);
    }
  }

  @Override
  public void setOurRetrieval(OffsetDateTime ourRetrieval) {
    feature.getProperties().put(OUR_CREATION_DATE, ourRetrieval);
  }
  public GeoJsonObject getGeometry() {
    return feature.getGeometry();
  }

  public Feature getGenericGeoJson() {
    return feature;
  }

  @Override
  public int hashCode() {
    return feature.hashCode();
  }
}
