package com.dynacore.livemap.guidancesign.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class Properties {

    @JsonProperty("Name")
    String name;
    @JsonProperty("PubDate")
    LocalDateTime pubDate;
    @JsonProperty("Type")
    String type;
    @JsonProperty("State")
    String state;
    @JsonProperty("Removed")
    String removed;
    @JsonProperty("ParkingguidanceDisplay")
    List<InnerDisplayModel> innerDisplayModelList;

    private Properties(Builder builder) {
        name = builder.name;
        pubDate = builder.pubDate;
        type = builder.type;
        state = builder.state;
        removed = builder.removed;
        innerDisplayModelList = builder.innerDisplayModelList;
    }

    public void setInnerDisplayModelList(List<InnerDisplayModel> innerDisplayModelList) {
        this.innerDisplayModelList = innerDisplayModelList;
    }

    static final class Builder {
        private String name;
        private LocalDateTime pubDate;
        private String type;
        private String state;
        private String removed;
        private List<InnerDisplayModel> innerDisplayModelList;

        Builder() {
        }

        Builder name(String val) {
            name = val;
            return this;
        }

        Builder pubDate(LocalDateTime val) {
            pubDate = val;
            return this;
        }

        Builder type(String val) {
            type = val;
            return this;
        }

        Builder state(String val) {
            state = val;
            return this;
        }

        Builder removed(String val) {
            removed = val;
            return this;
        }

        Builder innerDisplayModelList(List<InnerDisplayModel> val) {
            innerDisplayModelList = val;
            return this;
        }

        Properties build() {
            return new Properties(this);
        }
    }
}
