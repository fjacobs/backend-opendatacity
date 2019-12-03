package com.dynacore.livemap.traveltime.repo;

import lombok.*;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Immutable;
import org.springframework.data.relational.core.mapping.Table;

import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.Objects;


@Table
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class TravelTimeEntity {

    @Id
    private Integer pkey;

    private String id;
    private String name;

    private OffsetDateTime pubDate;
    private OffsetDateTime retrievedFromThirdParty;
    private String type;

    private int length;
    private int travel_time;
    private int velocity;

    private TravelTimeEntity(Builder builder) {
        setPkey(null);
        setId(builder.id);
        setName(builder.name);
        setPubDate(builder.pubDate);
        setRetrievedFromThirdParty(builder.retrievedFromThirdParty);
        setType(builder.type);
        setLength(builder.length);
        setTravel_time(builder.travelTime);
        setVelocity(builder.velocity);
    }


    public static final class Builder {
        private String id;
        private String name;
        private OffsetDateTime pubDate;
        private OffsetDateTime retrievedFromThirdParty;
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
            pubDate= OffsetDateTime.parse(val);
            return this;
        }

        public Builder retrievedFromThirdParty(String val) {
            retrievedFromThirdParty= OffsetDateTime.parse(val);
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TravelTimeEntity that = (TravelTimeEntity) o;
        return length == that.length &&
                travel_time == that.travel_time &&
                velocity == that.velocity &&
                Objects.equals(pkey, that.pkey) &&
                id.equals(that.id) &&
                Objects.equals(name, that.name) &&
                pubDate.equals(that.pubDate) &&
                Objects.equals(retrievedFromThirdParty, that.retrievedFromThirdParty) &&
                Objects.equals(type, that.type);
    }

    public Integer getPkey() {
        return pkey;
    }
    public void setPkey(Integer pkey) {
        this.pkey = pkey;
    }

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public OffsetDateTime getPubDate() { return pubDate; }
    public void setPubDate(OffsetDateTime pubDate) {
        this.pubDate = pubDate;
    }
    public OffsetDateTime getRetrievedFromThirdParty() {
        return retrievedFromThirdParty;
    }
    public void setRetrievedFromThirdParty(OffsetDateTime retrievedFromThirdParty) {
        this.retrievedFromThirdParty = retrievedFromThirdParty;
    }
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
    public int getLength() {
        return length;
    }
    public void setLength(int length) {
        this.length = length;
    }
    public int getTravel_time() {
        return travel_time;
    }
    public void setTravel_time(int travel_time) {
        this.travel_time = travel_time;
    }
    public int getVelocity() {
        return velocity;
    }
    public void setVelocity(int velocity) {
        this.velocity = velocity;
    }


}

