package com.dynacore.livemap.core.model;

import com.dynacore.livemap.traveltime.domain.TravelTimeFeature;

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
public class TrafficDTO extends HashMap<String, Object> {
  public void setId(String id) {
    put(TrafficFeature.ID, id);
  }

  public String getId() {
    return (String) get(TrafficFeature.ID);
  }

  public void setName(String name) {
    put(TrafficFeature.NAME, name);
  }

  public OffsetDateTime getPubDate() {
    Object x = get(TravelTimeFeature.THEIR_RETRIEVAL);
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

  public void setPubDate(OffsetDateTime pubDate) {
    put(TrafficFeature.THEIR_RETRIEVAL, pubDate);
  }

  public OffsetDateTime getSameSince() {
    return (OffsetDateTime) get(TrafficFeature.SAME_SINCE);
  }

  public OffsetDateTime getOurRetrieval() {
    return (OffsetDateTime) get(TrafficFeature.OUR_RETRIEVAL);
  }

  public void setOurRetrieval(OffsetDateTime ourRetrieval) {
    put(TrafficFeature.OUR_RETRIEVAL, ourRetrieval);
  }

  public void setSameSince(OffsetDateTime pubDate) {
    put(TrafficFeature.SAME_SINCE, pubDate);
  }
}
