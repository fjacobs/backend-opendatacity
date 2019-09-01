package com.dynacore.livemap.entity.jsonrepresentations;

import java.util.List;

/* This class is a GeoJSON FeatureCollection as described in: https://tools.ietf.org/html/rfc7946
    * Member types must be public for population by rest client mapper.
 */

public class GeoJsonCollection<T> {

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
