package com.dynacore.livemap.entity.hibernate;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;


@Entity
@Table(name = "parkinglogdata")
public class ParkingLogData implements Serializable {

    @Id
    private String id;
    private String name;
	@Id
    private LocalDateTime pubDate;
    private String type;
    private String state;
    private int freeSpaceShort;
    private int freeSpaceLong;
    private int shortCapacity;
    private int longCapacity;

    public ParkingLogData() {
    }

    public ParkingLogData(String id, String name, LocalDateTime pubDate, String type, String state, int freeSpaceShort,
                          int freeSpaceLong, int shortCapacity, int longCapacity) {

        this.id = id;
        this.name = name;
        this.pubDate = pubDate;
        this.type = type;
        this.state = state;
        this.freeSpaceShort = freeSpaceShort;
        this.freeSpaceLong = freeSpaceLong;
        this.shortCapacity = shortCapacity;
        this.longCapacity = longCapacity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ParkingLogData)) return false;
        ParkingLogData that = (ParkingLogData) o;
        return Objects.equals(getId(), that.getId()) &&
                Objects.equals(getPubDate(), that.getPubDate());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getPubDate());
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

    public LocalDateTime getPubDate() {
        return pubDate;
    }

    public void setPubDate(LocalDateTime pubDate) {
        this.pubDate = pubDate;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public int getFreeSpaceShort() {
        return freeSpaceShort;
    }

    public void setFreeSpaceShort(int freeSpaceShort) {
        this.freeSpaceShort = freeSpaceShort;
    }

    public int getFreeSpaceLong() {
        return freeSpaceLong;
    }

    public void setFreeSpaceLong(int freeSpaceLong) {
        this.freeSpaceLong = freeSpaceLong;
    }

    public int getShortCapacity() {
        return shortCapacity;
    }

    public void setShortCapacity(int shortCapacity) {
        this.shortCapacity = shortCapacity;
    }

    public int getLongCapacity() {
        return longCapacity;
    }

    public void setLongCapacity(int longCapacity) { this.longCapacity = longCapacity; }
}
