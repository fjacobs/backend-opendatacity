package com.dynacore.livemap.core.geojson;

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

  @JsonIgnore
  public void setName(String name) {
    getProperties().put(NAME, name);
  }

  @JsonIgnore
  public OffsetDateTime getPubDate() {
    return (OffsetDateTime) getProperties().get(THEIR_RETRIEVAL);
  }

  @JsonIgnore
  public void setPubDate(OffsetDateTime pubDate) {
    getProperties().put(THEIR_RETRIEVAL, pubDate);
  }

  @JsonIgnore
  public OffsetDateTime getSameSince() {
    return (OffsetDateTime) getProperties().get(SAME_SINCE);
  }

  @JsonIgnore
  public void setSameSince(OffsetDateTime date) {
    getProperties().put(SAME_SINCE, date);
  }

  @JsonIgnore
  public OffsetDateTime getOurRetrieval() {
    return (OffsetDateTime) getProperties().get(OUR_RETRIEVAL);
  }

  @JsonIgnore
  public void setOurRetrieval(OffsetDateTime ourRetrieval) {
    getProperties().put(OUR_RETRIEVAL, ourRetrieval);
  }
}
