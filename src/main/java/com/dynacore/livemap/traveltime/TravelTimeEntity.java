package com.dynacore.livemap.traveltime;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Table;
import java.time.LocalDateTime;


@Getter
@Setter
@Table(name = "traveltime")
class TravelTimeEntity {

    private String id;
    private String name;

    private LocalDateTime pub_date;
    private LocalDateTime retrieved_from_third_party;

    private String type;
    private int length;
    private int travel_time;
    private int velocity;

    public TravelTimeEntity() {
    }

    private TravelTimeEntity(Builder builder) {
        setId(builder.id);
        setName(builder.name);
        setPub_date(builder.pubDate);
        setRetrieved_from_third_party(builder.retrievedFromThirdParty);
        setType(builder.type);
        setLength(builder.length);
        setTravel_time(builder.travelTime);
        setVelocity(builder.velocity);
    }


    public static final class Builder {
        private String id;
        private String name;
        private LocalDateTime pubDate;
        private LocalDateTime retrievedFromThirdParty;
        private String type;
        private int length;
        private int travelTime;
        private int velocity;

        public Builder() {
        }

        public Builder id(String val) {
            id = val;
            return this;
        }

        public Builder name(String val) {
            name = val;
            return this;
        }

        public Builder pubDate(String val) {
            LocalDateTime timeStamp = LocalDateTime.parse(val.substring(0, val.length() - 1));
            pubDate = timeStamp;
            return this;
        }

        public Builder retrievedFromThirdParty(String val) {
            LocalDateTime timeStamp = LocalDateTime.parse(val.substring(0, val.length() - 1));
            retrievedFromThirdParty = timeStamp;
            return this;
        }

        public Builder type(String val) {
            type = val;
            return this;
        }

        public Builder length(int val) {
            length = val;
            return this;
        }

        public Builder travelTime(int val) {
            travelTime = val;
            return this;
        }

        public Builder velocity(int val) {
            velocity = val;
            return this;
        }

        public TravelTimeEntity build() {
            return new TravelTimeEntity(this);
        }
    }
}

