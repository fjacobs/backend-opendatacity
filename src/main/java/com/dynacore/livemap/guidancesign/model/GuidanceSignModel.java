package com.dynacore.livemap.guidancesign.model;

import com.dynacore.livemap.common.model.Feature;
import com.dynacore.livemap.common.model.Geometry;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.*;

import static java.util.stream.Collectors.toList;

@Getter @Setter
public class GuidanceSignModel implements Feature {

    @JsonProperty("Id")
    private UUID id;
    @JsonProperty("type")
    private String type;
    @JsonProperty("geometry")
    private Geometry geometry;

    private Properties properties;

    @JsonProperty("properties")
    private void unpackNested(Map<String, Object> prop) {
        List<InnerDisplayModel> innerList = ((ArrayList<LinkedHashMap<String, String>>) prop.get("ParkingguidanceDisplay")).stream()
                .map(dispMap -> new InnerDisplayModel.Builder()
                        .id(UUID.fromString( dispMap.get("Id") ) )
                        .description(dispMap.get("Description"))
                        .output(dispMap.get("Output"))
                        .outputDescription(dispMap.get("OutputDescription"))
                        .type(dispMap.get("Type"))
                        .build())
                .collect(toList());

        String temp = (String) prop.get("PubDate");

        properties = new Properties.Builder()
                .name((String) prop.get("Name"))
                .pubDate(LocalDateTime.parse(temp.substring(0, temp.length() - 1)))
                .type((String) prop.get("Type"))
                .removed((String) prop.get("Removed"))
                .state((String) prop.get("State"))
                .innerDisplayModelList(innerList)
                .build();
    }

    @JsonIgnore
    public String getName() {
        return properties.name;
    }

    @JsonIgnore
    public LocalDateTime getPubDate() {
        return properties.pubDate;
    }

    @JsonIgnore
    public String getPropType() {
        return properties.type;
    }

    @JsonIgnore
    public String getRemoved() {
        return properties.removed;
    }

    @JsonIgnore
    public String getState() {
        return properties.state;
    }

}
