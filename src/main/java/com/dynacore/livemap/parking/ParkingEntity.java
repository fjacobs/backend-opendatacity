package com.dynacore.livemap.parking;

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
@Table(name = "PARKING")
@Getter @Setter
@EqualsAndHashCode(of = {"id", "pubDate"} )
public class ParkingEntity implements Serializable {

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

    ParkingEntity() {
    }

    ParkingEntity(String id, String name, LocalDateTime pubDate, String state, int freeSpaceShort,
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
}
