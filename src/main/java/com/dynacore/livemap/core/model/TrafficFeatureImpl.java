package com.dynacore.livemap.core.model;

import com.dynacore.livemap.core.repository.TrafficEntity;
import com.fasterxml.jackson.annotation.JsonTypeName;
import org.geojson.Feature;
import org.geojson.GeoJsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.OffsetDateTime;

//Typesafe wrapper for GeoJson Feature
@JsonTypeName("Feature")
public class TrafficFeatureImpl implements TrafficFeature {
  Logger log = LoggerFactory.getLogger(TrafficFeatureImpl.class);
  protected Feature feature;

  public TrafficFeatureImpl() {
    feature = new Feature();
  }

  public TrafficFeatureImpl(TrafficEntity entity) {
    feature = new Feature();
    setId(entity.getId());
    setName(entity.getName());
    setPubDate(entity.getPubDate());
    setOurRetrieval(entity.getOurRetrieval());
  }
  // Different suppliers use different keys for the data publication timestamp:
  public TrafficFeatureImpl(Feature feature, String pubDateKey) {
    try {
      this.feature = feature;

      if (feature.getProperties().containsKey(pubDateKey) ) {
        feature.getProperties().put(THEIR_RETRIEVAL, feature.getProperties().get(pubDateKey));// new key
        feature.getProperties().remove(pubDateKey);
      } else if (!feature.getProperties().containsKey(THEIR_RETRIEVAL) ) {
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
//    if (feature.getProperties().get(OUR_CREATION_DATE) instanceof String) {
//      return OffsetDateTime.parse((String) feature.getProperties().get(OUR_CREATION_DATE));
//    } else {
//      return (OffsetDateTime) feature.getProperties().get(OUR_CREATION_DATE);
//    }
    return null;
  }

  @Override
  public void setOurRetrieval(OffsetDateTime ourRetrieval) {
//    feature.getProperties().put(OUR_CREATION_DATE, ourRetrieval);
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
