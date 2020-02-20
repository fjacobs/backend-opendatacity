package com.dynacore.livemap.guidancesign.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Table(name = "inner_display_entity")
@ToString
@NoArgsConstructor
@Getter
@Setter
public class InnerDisplayEntity {

    @Id
    private Integer fk;
    private String id;
    private String outputDescription;
    private String description;
    private String type;
    private String output;

    private InnerDisplayEntity(InnerDisplayEntity.Builder builder) {
        setId(builder.innerDisplayId);
        setOutputDescription(builder.outputDescription);
        setDescription(builder.description);
        setType(builder.type);
        setOutput(builder.output);
        setFk(builder.fk);
    }

    public static final class Builder {
        private Integer fk;
        private String innerDisplayId;
        private String outputDescription;
        private String description;
        private String type;
        private String output;
        public Builder() {}

        public  InnerDisplayEntity.Builder innerDisplayId(String val) {
            innerDisplayId = val;
            return this;
        }


        public InnerDisplayEntity.Builder outputDescription(String val) {
            outputDescription = val;
            return this;
        }

        public  InnerDisplayEntity.Builder description(String val) {
            description = val;
            return this;
        }

        public InnerDisplayEntity.Builder type(String val) {
            type = val;
            return this;
        }

        public  InnerDisplayEntity.Builder fk(Integer val) {
            fk = val;
            return this;
        }


        public InnerDisplayEntity.Builder output(String val) {
            output = val;
            return this;
        }

        public InnerDisplayEntity build() {
            return new InnerDisplayEntity(this);
        }
    }
}
