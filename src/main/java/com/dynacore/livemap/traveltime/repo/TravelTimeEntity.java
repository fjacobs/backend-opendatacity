package com.dynacore.livemap.traveltime.repo;

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
@AllArgsConstructor
@NoArgsConstructor @ToString
public class TravelTimeEntity {

    public static final String ID = "Id";
    public static final String NAME = "Name";
    public static final String TYPE = "Type";
    public static final String TRAVEL_TIME = "Traveltime";
    public static final String LENGTH = "Length";
    public static final String VELOCITY = "Velocity";
    public static final String OUR_RETRIEVAL = "retrievedFromThirdParty";
    public static final String THEIR_RETRIEVAL = "Timestamp";
    public static final String DYNACORE_ERRORS = "dynacoreErrors";

    private static final Logger log = LoggerFactory.getLogger(TravelTimeEntity.class);

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

    public TravelTimeEntity(Feature travelTime) {

        try {
            setPkey(null);
            if(travelTime.getId() != null) {
                setId(travelTime.getId());
            } else {
                log.warn("Feature doesn't have an ID: " + travelTime.toString());
            }
            setName((String) travelTime.getProperties().get(NAME));
            if(travelTime.getProperties().containsKey(THEIR_RETRIEVAL))
                setPubDate(OffsetDateTime.parse(travelTime.getProperties().get(THEIR_RETRIEVAL).toString()));
            else {
                log.warn("Feature doesn't have a pub date: " + travelTime.toString());
            }
            setRetrievedFromThirdParty(OffsetDateTime.parse(travelTime.getProperties().get(OUR_RETRIEVAL).toString()));
            setType((String) travelTime.getProperties().get(TYPE));
            setTravel_time((int) travelTime.getProperties().get(TRAVEL_TIME));
            setVelocity((int) travelTime.getProperties().get(VELOCITY));
            setLength((int) travelTime.getProperties().get(LENGTH));

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getMessage());
        }
    }
}

