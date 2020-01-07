package com.dynacore.livemap.traveltime.repo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
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
@NoArgsConstructor
public class TravelTimeEntity {

    private static final String ID = "Id";
    private static final String NAME = "Name";
    private static final String TYPE = "Type";
    private static final String TRAVEL_TIME = "Traveltime";
    private static final String LENGTH = "Length";
    private static final String VELOCITY = "Velocity";
    private static final String OUR_RETRIEVAL = "retrievedFromThirdParty";
    private static final String THEIR_RETRIEVAL = "Timestamp";
    private static final String DYNACORE_ERRORS = "dynacoreErrors";

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

