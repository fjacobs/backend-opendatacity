package com.dynacore.livemap.guidancesign.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;


@Entity
@Table(name = "GUIDANCE_DISPLAY")
@Getter @Setter
public class InnerDisplayEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    @ManyToOne
    @JoinColumn(name = "guidancesign_id", nullable = false)
    private GuidanceSignEntity guidanceSignEntity;

    private String outputDescription;
    private String description;
    private String type;
    private String output;

    public InnerDisplayEntity() {}

    public InnerDisplayEntity(GuidanceSignEntity guidanceSignEntity, String outputDescription, String description, String type, String output) {
        this.guidanceSignEntity = guidanceSignEntity;
        this.outputDescription = outputDescription;
        this.description = description;
        this.type = type;
        this.output = output;
    }

    private InnerDisplayEntity(Builder builder) {
        setId(builder.id);
        setGuidanceSignEntity(builder.guidanceSignEntity);
        setOutputDescription(builder.outputDescription);
        setDescription(builder.description);
        setType(builder.type);
        setOutput(builder.output);
    }

    public static final class Builder {
        private long id;
        private GuidanceSignEntity guidanceSignEntity;
        private String outputDescription;
        private String description;
        private String type;
        private String output;

        Builder() {
        }

        public Builder id(long val) {
            id = val;
            return this;
        }

        Builder guidanceSignEntity(GuidanceSignEntity val) {
            guidanceSignEntity = val;
            return this;
        }

        Builder outputDescription(String val) {
            outputDescription = val;
            return this;
        }

        Builder description(String val) {
            description = val;
            return this;
        }

        Builder type(String val) {
            type = val;
            return this;
        }

        Builder output(String val) {
            output = val;
            return this;
        }

        InnerDisplayEntity build() {
            return new InnerDisplayEntity(this);
        }
    }
}
