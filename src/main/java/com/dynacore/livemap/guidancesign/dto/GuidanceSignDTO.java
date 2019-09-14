package com.dynacore.livemap.guidancesign.dto;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Set;


@Entity
@Table(name = "GUIDANCE_SIGN")
@Getter @Setter
public class GuidanceSignDTO {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "guidancesign_id")
    private long id;

    //Flatted from model.properties:
    private String Name;
    private String PubDate;
    private String State;

    @OneToMany(mappedBy = "guidanceSignDTO")
    private Set<InnerDisplayDTO> innerDisplays;

    private GuidanceSignDTO(Builder builder) {
        setId(builder.id);
        setName(builder.Name);
        setPubDate(builder.PubDate);
        setState(builder.State);
        setInnerDisplays(builder.innerDisplays);
    }

    public static final class Builder {
        private long id;
        private String Name;
        private String PubDate;
        private String State;
        private Set<InnerDisplayDTO> innerDisplays;

        public Builder() {
        }

        public Builder id(long val) {
            id = val;
            return this;
        }

        public Builder Name(String val) {
            Name = val;
            return this;
        }

        public Builder PubDate(String val) {
            PubDate = val;
            return this;
        }

        public Builder State(String val) {
            State = val;
            return this;
        }

        public Builder innerDisplays(Set<InnerDisplayDTO> val) {
            innerDisplays = val;
            return this;
        }

        public GuidanceSignDTO build() {
            return new GuidanceSignDTO(this);
        }
    }
}