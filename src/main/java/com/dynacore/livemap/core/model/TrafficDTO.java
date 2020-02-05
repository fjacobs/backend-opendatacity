package com.dynacore.livemap.core.model;

import com.dynacore.livemap.traveltime.domain.TravelTimeFeature;
import lombok.NoArgsConstructor;

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
public abstract class TrafficDTO extends HashMap<Object, Object> {
  public void setId(String id) {
    put(TravelTimeFeature.ID, id);
  }

  public String getId() {
    return (String) get(TravelTimeFeature.ID);
  }

  public void setName(String name) {
    put(TravelTimeFeature.NAME, name);
  }

  public OffsetDateTime getPubDate() {
    return (OffsetDateTime) get(TravelTimeFeature.THEIR_RETRIEVAL);
  }

  public void setPubDate(OffsetDateTime pubDate) {
    put(TravelTimeFeature.THEIR_RETRIEVAL, pubDate);
  }

  public OffsetDateTime getSameSince() {
    return (OffsetDateTime) get(TravelTimeFeature.SAME_SINCE);
  }

  public OffsetDateTime getOurRetrieval() {
    return (OffsetDateTime) get(TravelTimeFeature.OUR_RETRIEVAL);
  }

  public void setOurRetrieval(OffsetDateTime ourRetrieval) {
    put(TravelTimeFeature.OUR_RETRIEVAL, ourRetrieval);
  }

  public void setSameSince(OffsetDateTime pubDate) {
    put(TravelTimeFeature.SAME_SINCE, pubDate);
  }
}
