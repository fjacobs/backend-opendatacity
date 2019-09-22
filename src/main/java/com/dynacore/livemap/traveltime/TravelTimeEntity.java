package com.dynacore.livemap.traveltime;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Immutable;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.time.LocalDateTime;


@Entity
@Immutable
@Table(name = "TRAVEL_TIME")
@Getter @Setter
@EqualsAndHashCode(of = {"id", "pubDate"} )
class TravelTimeEntity implements Serializable {

    @Id
    private String id;
    private String name;

    @Id
    private LocalDateTime pubDate;
    private LocalDateTime retrievedFromThirdParty;

    private String type;
    private int length;
    private int travelTime;
    private int velocity;

    private TravelTimeEntity(Builder builder) {
        setId(builder.id);
        setName(builder.name);
        setPubDate(builder.pubDate);
        setRetrievedFromThirdParty(builder.retrievedFromThirdParty);
        setType(builder.type);
        setLength(builder.length);
        setTravelTime(builder.travelTime);
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

        public Builder retrievedFromThirdParty(LocalDateTime val) {
            retrievedFromThirdParty = val;
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

