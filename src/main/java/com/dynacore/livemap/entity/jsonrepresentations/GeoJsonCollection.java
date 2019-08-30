package com.dynacore.livemap.entity.jsonrepresentations;

import java.util.List;

public class GeoJsonCollection<T> {
    //Member types must be public for population by restTemplate.getForObject().
    public String type;
    public List<T> features;

    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
    public List<T> getFeatures() {
        return features;
    }
    public void setFeatures(List<T> features) {
        this.features = features;
    }
}
