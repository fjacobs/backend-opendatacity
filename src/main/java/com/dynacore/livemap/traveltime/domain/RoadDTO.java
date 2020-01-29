package com.dynacore.livemap.traveltime.domain;

import java.time.OffsetDateTime;
import java.util.HashMap;

// Use to send updated Feature properties
// This DTO extends from HashMap to keep the json (or ideally cbor)
// as small as possible when sending through the network
//
//ie:
// {
//    id: <id>,
//    velocity: <changed velocity>
// }

public class RoadDTO extends HashMap<Object,Object> {

    public void setId(String id) {
        put(RoadFeature.ID, id);
    }
    public String getId() {
         return (String) get(RoadFeature.ID);
    }

    public void setName(String name) {
        put(RoadFeature.NAME, name);
    }


    public void setType(String type) {
        put(RoadFeature.TYPE, type);
    }

    public Integer getTravelTime() {
        return (Integer) get(RoadFeature.TRAVEL_TIME);
    }

    public void setTravelTime(Integer travelTime) {
        put(RoadFeature.TRAVEL_TIME, travelTime);
    }

    public Integer getLength() {
        return (Integer) get(RoadFeature.LENGTH);
    }

    public void setLength(Integer length) {
        put(RoadFeature.LENGTH, length);
    }

    public Integer getVelocity() {
        return (Integer) get(RoadFeature.VELOCITY);
    }

    public void setVelocity(Integer velocity) {
        put(RoadFeature.VELOCITY, velocity);
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

    public String getType() {
        return (String) get(RoadFeature.TYPE);
    }


    public OffsetDateTime getSameSince() {
        return (OffsetDateTime) get(RoadFeature.SAME_SINCE);
    }

    public void setSameSince(OffsetDateTime pubDate) {
        put(RoadFeature.SAME_SINCE, pubDate);
    }
}
