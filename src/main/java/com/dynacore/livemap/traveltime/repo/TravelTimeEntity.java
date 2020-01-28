package com.dynacore.livemap.traveltime.repo;

import com.dynacore.livemap.traveltime.domain.RoadFeature;
import lombok.*;
import org.geojson.Feature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.OffsetDateTime;


@Table
@Getter
@Setter
@NoArgsConstructor @ToString
public class TravelTimeEntity {

    private static final Logger log = LoggerFactory.getLogger(TravelTimeEntity.class);

    @Id
    private Integer pkey;

    private String id;
    private String name;

    private OffsetDateTime pubDate;
    private OffsetDateTime retrievedFromThirdParty;
    private String type;

    private Integer length;
    private Integer travel_time;
    private Integer velocity;

    public TravelTimeEntity(Integer pkey, String id, String name, OffsetDateTime pubDate, OffsetDateTime retrievedFromThirdParty, String type, Integer length, Integer travel_time, Integer velocity) {
        this.pkey = pkey;
        this.id = id;
        this.name = name;
        this.pubDate = pubDate;
        this.retrievedFromThirdParty = retrievedFromThirdParty;
        this.type = type;
        this.length = length;
        this.travel_time = travel_time;
        this.velocity = velocity;
    }

    public TravelTimeEntity(RoadFeature travelTime) {

        try {
            setPkey(null);
            if(travelTime.getId() != null) {
                setId(travelTime.getId());
            } else {
                log.warn("Feature doesn't have an ID: " + travelTime.toString());
            }
            setName(travelTime.getName());
            if(travelTime.getPubDate() != null)
                setPubDate(travelTime.getPubDate());
            else {
                log.warn("Feature doesn't have a pub date: " + travelTime.toString());
            }
            setRetrievedFromThirdParty(travelTime.getRetrievedFromThirdParty());
            setType(travelTime.getType());
            setTravel_time(travelTime.getTravelTime());
            setVelocity(travelTime.getVelocity());
            setLength(travelTime.getLength());

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getMessage());
        }
    }
}

