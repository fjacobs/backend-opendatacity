package com.dynacore.livemap.traveltime.domain;

import org.geojson.Feature;

import java.time.OffsetDateTime;

// Wrapper class for GeoJson Feature
// See visitor package for methods.
public class RoadFeature extends Feature {

    public static final String ID = "Id";
    public static final String NAME = "Name";
    public static final String TYPE = "Type";
    public static final String TRAVEL_TIME = "Traveltime";
    public static final String LENGTH = "Length";
    public static final String VELOCITY = "Velocity";
    public static final String OUR_RETRIEVAL = "retrievedFromThirdParty";
    public static final String THEIR_RETRIEVAL = "pubDate";
    public static final String SAME_SINCE = "sameSince";

    public String getName() {
        return getProperty(NAME);
    }

    public OffsetDateTime getPubDate() {
        return getProperty(THEIR_RETRIEVAL);
    }

    public OffsetDateTime getRetrievedFromThirdParty() {
        return getProperty(OUR_RETRIEVAL);
    }

    public String getType() {
        return getProperty(TYPE);
    }

    public Integer getLength() {
        return getProperty(LENGTH);
    }

    public Integer getTravelTime() {
        return getProperty(TRAVEL_TIME);
    }

    public Integer getVelocity() {
        return getProperty(VELOCITY);
    }

    public void setName(String name) {
        getProperties().put(NAME, name);
    }

    public void setPubDate(OffsetDateTime pubDate) {
        getProperties().put(THEIR_RETRIEVAL, pubDate);
    }

    public void setRetrievedFromThirdParty(OffsetDateTime retrievedFromThirdParty) {
        getProperties().put(OUR_RETRIEVAL, retrievedFromThirdParty);
    }

    public void setType(String type) {
        getProperties().put(TYPE, type);
    }

    public void setLength(Integer length) {
        getProperties().put(LENGTH, length);
    }

    public void setTravel_time(Integer travelTime) {
        getProperties().put(TRAVEL_TIME, travelTime);
    }

    public void setVelocity(Integer velocity) {
        getProperties().put(VELOCITY, velocity);
    }
}
