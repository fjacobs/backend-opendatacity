package com.dynacore.livemap.core.model;

import com.dynacore.livemap.traveltime.domain.TravelTimeFeatureImpl;

import java.time.OffsetDateTime;
import java.util.HashMap;

// Send updated Feature properties after initial subscription
// This DTO extends from HashMap to keep the json (or ideally cbor)
// as small as possible when sending through the network
//
// ie:
// {
//    id: <id>,
//    velocity: <changed velocity>
// }
public class TrafficMapDTO extends HashMap<String, Object> implements TrafficDTO {
  @Override
  public void setId(String id) {
    put(TrafficFeatureImpl.ID, id);
  }

  @Override
  public String getId() {
    return (String) get(TrafficFeatureImpl.ID);
  }

  public void setName(String name) {
    put(TrafficFeatureImpl.NAME, name);
  }

  @Override
  public OffsetDateTime getPubDate() {
    Object x = get(TravelTimeFeatureImpl.THEIR_RETRIEVAL);
    assert x != null;
    if (x instanceof OffsetDateTime) {
      return (OffsetDateTime) x;
    }
    try {
      if (x instanceof String) {
        return OffsetDateTime.parse((String) x);
      }
    } catch (Exception error) {
      System.out.println(error.getMessage());
      error.printStackTrace();
    }
    assert false;
    return null;
  }

  @Override
  public void setPubDate(OffsetDateTime pubDate) {
    put(TrafficFeatureImpl.THEIR_RETRIEVAL, pubDate);
  }

  public OffsetDateTime getSameSince() {
    return (OffsetDateTime) get(TrafficFeatureImpl.SAME_SINCE);
  }

  public OffsetDateTime getOurRetrieval() {
    return (OffsetDateTime) get(TrafficFeatureImpl.OUR_CREATION_DATE);
  }

  public void setOurRetrieval(OffsetDateTime ourRetrieval) {
    put(TrafficFeatureImpl.OUR_CREATION_DATE, ourRetrieval);
  }

  public void setSameSince(OffsetDateTime pubDate) {
    put(TrafficFeatureImpl.SAME_SINCE, pubDate);
  }
}
