package com.dynacore.livemap.guidancesign.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;


@Table(name = "guidance_inner_display")
@NoArgsConstructor
@Getter
@Setter
public class InnerDisplayEntity {

    protected Integer pkey;
    private String id;
    private String outputDescription;
    private String description;
    private String type;
    private String output;
    private GuidanceSignEntity parentRef;


    private InnerDisplayEntity(InnerDisplayEntity.Builder builder) {
        setId(builder.innerDisplayId);
        setOutputDescription(builder.outputDescription);
        setDescription(builder.description);
        setType(builder.type);
        setOutput(builder.output);
    }

    static final class Builder {
        private String innerDisplayId;
        private String outputDescription;
        private String description;
        private String type;
        private String output;
        private GuidanceSignEntity parentRef;
        Builder() {}

        InnerDisplayEntity.Builder innerDisplayId(String val) {
            innerDisplayId = val;
            return this;
        }

        InnerDisplayEntity.Builder guidanceSignEntity(GuidanceSignEntity val) {
            parentRef = val;
            return this;
        }

        InnerDisplayEntity.Builder outputDescription(String val) {
            outputDescription = val;
            return this;
        }

        InnerDisplayEntity.Builder description(String val) {
            description = val;
            return this;
        }

        InnerDisplayEntity.Builder type(String val) {
            type = val;
            return this;
        }

        InnerDisplayEntity.Builder output(String val) {
            output = val;
            return this;
        }

        InnerDisplayEntity build() {
            return new InnerDisplayEntity(this);
        }
    }
}
