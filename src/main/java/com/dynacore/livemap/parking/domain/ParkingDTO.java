package com.dynacore.livemap.parking.domain;

import com.dynacore.livemap.traveltime.domain.RoadFeature;

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

public class ParkingDTO extends HashMap<Object, Object> {

  public void setId(String id) {
    put(RoadFeature.ID, id);
  }

  public String getId() {
    return (String) get(RoadFeature.ID);
  }

  public void setName(String name) {
    put(RoadFeature.NAME, name);
  }

  public OffsetDateTime getPubDate() {
    return (OffsetDateTime) get(RoadFeature.THEIR_RETRIEVAL);
  }

  public void setPubDate(OffsetDateTime pubDate) {
    put(RoadFeature.THEIR_RETRIEVAL, pubDate);
  }

  public OffsetDateTime getOurRetrieval() {
    return (OffsetDateTime) get(RoadFeature.OUR_RETRIEVAL);
  }

  public void setOurRetrieval(OffsetDateTime ourRetrieval) {
    put(RoadFeature.OUR_RETRIEVAL, ourRetrieval);
  }

  public OffsetDateTime getSameSince() {
    return (OffsetDateTime) get(RoadFeature.SAME_SINCE);
  }

  public void setSameSince(OffsetDateTime pubDate) {
    put(RoadFeature.SAME_SINCE, pubDate);
  }
}
