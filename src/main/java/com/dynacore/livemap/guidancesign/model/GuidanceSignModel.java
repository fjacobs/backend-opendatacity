package com.dynacore.livemap.guidancesign.model;

import com.dynacore.livemap.common.model.Feature;
import com.dynacore.livemap.common.model.Geometry;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;

@ToString
public class GuidanceSignModel implements Feature {

    @JsonProperty("Id")
    private String id;
    @JsonProperty("type")
    private String type;
    @JsonProperty("geometry")
    private Geometry geometry;

    private Properties properties;

    @JsonProperty("properties")
    private void unpackNested(Map<String, Object> prop) {
        List<InnerDisplayModel> innerList = ((ArrayList<LinkedHashMap<String, String>>) prop.get("ParkingguidanceDisplay")).stream()
                .map(dispMap -> new InnerDisplayModel.Builder().id(dispMap.get("Id"))
                .description(dispMap.get("Description"))
                .output(dispMap.get("Output"))
                .outputDescription(dispMap.get("OutputDescription"))
                .type(dispMap.get("Type"))
                .build())
                .collect(toList());


        String temp = (String) prop.get("PubDate");
        String pubDate = temp.substring(0, temp.length() - 1);

        properties = new Properties.Builder()
                .name((String) prop.get("Name"))
                .pubDate(LocalDateTime.parse(pubDate))
                .type((String) prop.get("Type"))
                .removed((String) prop.get("Removed"))
                .state((String) prop.get("State"))
                .innerDisplayModelList(innerList)
                .build();
    }


    public Geometry getGeometry() {
        return geometry;
    }

    public void setGeometry(Geometry geometry) {
        this.geometry = geometry;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Properties getProperties() {
        return properties;
    }

    public String getType() {
        return type;
    }

    public void setType(Properties properties) {
        this.properties = properties;
    }

    public void setType(String type) {
        this.type = type;
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
