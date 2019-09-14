package com.dynacore.livemap.guidancesign.dto;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;


@Entity
@Table(name = "GUIDANCE_DISPLAY")
@Getter @Setter
public class InnerDisplayDTO {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    @ManyToOne
    @JoinColumn(name = "guidancesign_id", nullable = false)
    private GuidanceSignDTO guidanceSignDTO;

    private String OutputDescription;
    private String Description;
    private String Type;
    private String Output;

    private InnerDisplayDTO(Builder builder) {
        setId(builder.id);
        setGuidanceSignDTO(builder.guidanceSignDTO);
        setOutputDescription(builder.OutputDescription);
        setDescription(builder.Description);
        setType(builder.Type);
        setOutput(builder.Output);
    }

    public static final class Builder {
        private long id;
        private GuidanceSignDTO guidanceSignDTO;
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

        public Builder guidanceSignDTO(GuidanceSignDTO val) {
            guidanceSignDTO = val;
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

        public InnerDisplayDTO build() {
            return new InnerDisplayDTO(this);
        }
    }
}
