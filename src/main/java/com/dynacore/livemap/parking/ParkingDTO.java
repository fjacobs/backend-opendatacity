package com.dynacore.livemap.parking;

import org.springframework.data.annotation.Immutable;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Immutable
@Table(name = "parkinglogdata")
public class ParkingDTO implements Serializable {

    public ParkingDTO() {
    }

    @Id
    private String id;
    private String name;
    @Id
    private LocalDateTime pubDate;
    private String state;
    private int freeSpaceShort;
    private int freeSpaceLong;
    private int shortCapacity;
    private int longCapacity;

    ParkingDTO(String id, String name, LocalDateTime pubDate, String state, int freeSpaceShort,
               int freeSpaceLong, int shortCapacity, int longCapacity) {
        this.id = id;
        this.name = name;
        this.pubDate = pubDate;
        this.state = state;
        this.freeSpaceShort = freeSpaceShort;
        this.freeSpaceLong = freeSpaceLong;
        this.shortCapacity = shortCapacity;
        this.longCapacity = longCapacity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ParkingDTO)) return false;
        ParkingDTO that = (ParkingDTO) o;
        return Objects.equals(getId(), that.getId()) &&
                Objects.equals(getPubDate(), that.getPubDate());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getPubDate());
    }

    private String getId() {
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

    private LocalDateTime getPubDate() {
        return pubDate;
    }

    public void setPubDate(LocalDateTime pubDate) {
        this.pubDate = pubDate;
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

    public void setLongCapacity(int longCapacity) {
        this.longCapacity = longCapacity;
    }
}
