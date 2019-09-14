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

    private String OutputDescription;
    private String Description;
    private String Type;
    private String Output;

    public InnerDisplayEntity() {}

    public InnerDisplayEntity(GuidanceSignEntity guidanceSignEntity, String outputDescription, String description, String type, String output) {
        this.guidanceSignEntity = guidanceSignEntity;
        OutputDescription = outputDescription;
        Description = description;
        Type = type;
        Output = output;
    }

    private InnerDisplayEntity(Builder builder) {
        setId(builder.id);
        setGuidanceSignEntity(builder.guidanceSignEntity);
        setOutputDescription(builder.OutputDescription);
        setDescription(builder.Description);
        setType(builder.Type);
        setOutput(builder.Output);
    }

    public static final class Builder {
        private long id;
        private GuidanceSignEntity guidanceSignEntity;
        private String OutputDescription;
        private String Description;
        private String Type;
        private String Output;

        public Builder() {
        }

        public Builder id(long val) {
            id = val;
            return this;
        }

        public Builder guidanceSignEntity(GuidanceSignEntity val) {
            guidanceSignEntity = val;
            return this;
        }

        public Builder OutputDescription(String val) {
            OutputDescription = val;
            return this;
        }

        public Builder Description(String val) {
            Description = val;
            return this;
        }

        public Builder Type(String val) {
            Type = val;
            return this;
        }

        public Builder Output(String val) {
            Output = val;
            return this;
        }

        public InnerDisplayEntity build() {
            return new InnerDisplayEntity(this);
        }
    }
}
