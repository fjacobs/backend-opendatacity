package com.dynacore.livemap.traveltime;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Table;
import java.time.LocalDateTime;
import java.util.Objects;


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

    public LocalDateTime getPub_date() {
        return pub_date;
    }

    public void setPub_date(LocalDateTime pub_date) {
        this.pub_date = pub_date;
    }

    public LocalDateTime getRetrieved_from_third_party() {
        return retrieved_from_third_party;
    }

    public void setRetrieved_from_third_party(LocalDateTime retrieved_from_third_party) {
        this.retrieved_from_third_party = retrieved_from_third_party;
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


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TravelTimeEntity that = (TravelTimeEntity) o;
        return getLength() == that.getLength() &&
                getTravel_time() == that.getTravel_time() &&
                getVelocity() == that.getVelocity() &&
                Objects.equals(getId(), that.getId()) &&
                Objects.equals(getName(), that.getName()) &&
                Objects.equals(getPub_date(), that.getPub_date()) &&
                Objects.equals(getRetrieved_from_third_party(), that.getRetrieved_from_third_party()) &&
                Objects.equals(getType(), that.getType());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getName(), getPub_date(), getRetrieved_from_third_party(), getType(), getLength(), getTravel_time(), getVelocity());
    }
}

