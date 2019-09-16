package com.dynacore.livemap.guidancesign.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

//input
//    "Name":"A1006_A010_23,550-Re-1_P+R Bos en Lommer",
//    "PubDate":"2015-05-15T15:08:21.000Z",
//    "Removed":"false",
//    "Type":"guidancesign",
//    "State":"ok",
//    "ParkingguidanceDisplay":[
//       {
//          "Id":"000001094",
//          "OutputDescription":"VOL",
//          "Description":"A1006_VVX_P+R Bos en Lommer",
//          "Type":"VVX",
//          "Output":"VOL"
//       }
//    ]

@Getter @Setter
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

    public void setInnerDisplayModelList(ArrayList<InnerDisplayModel> innerDisplayModelList) {
        this.innerDisplayModelList = innerDisplayModelList;
    }

    public static final class Builder {
        private String name;
        private LocalDateTime pubDate;
        private String type;
        private String state;
        private String removed;
        private List<InnerDisplayModel> innerDisplayModelList;

        public Builder() {
        }

        public Builder name(String val) {
            name = val;
            return this;
        }

        public Builder pubDate(LocalDateTime val) {
            pubDate = val;
            return this;
        }

        public Builder type(String val) {
            type = val;
            return this;
        }

        public Builder state(String val) {
            state = val;
            return this;
        }

        public Builder removed(String val) {
            removed = val;
            return this;
        }

        public Builder innerDisplayModelList(List<InnerDisplayModel> val) {
            innerDisplayModelList = val;
            return this;
        }

        public Properties build() {
            return new Properties(this);
        }
    }
}
