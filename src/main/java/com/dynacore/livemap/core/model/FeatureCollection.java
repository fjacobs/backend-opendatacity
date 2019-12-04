package com.dynacore.livemap.core.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;
import java.util.List;

/* This class is a GeoJSON FeatureCollection as described in: https://tools.ietf.org/html/rfc7946
    * Member types must be public for population by rest client mapper.
 */

public class FeatureCollection<T extends Feature> {

    private String type;
    private List<T> features;
    @JsonProperty("dynacoreErrors")
    private String dynacoreErrors = "none";
    @JsonProperty("retrievedFromThirdParty")
    private LocalDateTime retrievedFromThirdParty;
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
    public List<T> getFeatures() {
        return features;
    }
    public void setFeatures(List <T> features) {
        this.features = features;
    }

    @JsonIgnore
    public void setErrorReport(String error) {
        dynacoreErrors = error;
    }
    @JsonIgnore
    public LocalDateTime getRetrievedFromThirdParty(){  return retrievedFromThirdParty; };

    public void setTimeOfRetrievalNow() {
        retrievedFromThirdParty = LocalDateTime.now();
        features.stream().forEach(feature->feature.getProperties().setTimeOfRetrievalNow());
    }


}
