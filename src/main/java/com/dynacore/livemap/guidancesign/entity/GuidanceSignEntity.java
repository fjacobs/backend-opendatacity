package com.dynacore.livemap.guidancesign.entity;

import com.dynacore.livemap.guidancesign.model.GuidanceSignModel;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;


@Entity
@Table(name = "GUIDANCE_SIGN")
@Getter @Setter
public class GuidanceSignEntity {

    @Transient
    Logger logger = LoggerFactory.getLogger(GuidanceSignEntity.class);

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "guidancesign_id")
    private long id;

    //Flatted from model.properties:
    private String Name;
    private LocalDateTime PubDate;
    private String State;

    @OneToMany(mappedBy = "guidanceSignEntity")
    private Set<InnerDisplayEntity> innerDisplays;

    public GuidanceSignEntity() {
    }

    public GuidanceSignEntity(GuidanceSignModel model) {
        try {
            Name = model.getName();
            PubDate = model.getPubDate();
            State = model.getState();
            innerDisplays = model.getProperties().getInnerDisplayModelList().stream()
                    .map(inner -> new InnerDisplayEntity.Builder()
                            .OutputDescription(inner.getOutputDescription())
                            .Output(inner.getOutput())
                            .Type(inner.getType())
                            .Description(inner.getDescription())
                            .guidanceSignEntity(this)
                            .build())
                    .collect(Collectors.toSet());
        } catch (Exception e) {
            logger.error("GuidanceSignDTO construction error: ", e);
        }
    }

    public GuidanceSignEntity(String name, LocalDateTime pubDate, String state, HashSet<InnerDisplayEntity> innerDisplays) {
        Name = name;
        PubDate = pubDate;
        State = state;
        this.innerDisplays = innerDisplays;
    }

    private GuidanceSignEntity(Builder builder) {
        setId(builder.id);
        setName(builder.Name);
        setPubDate(builder.PubDate);
        setState(builder.State);
        setInnerDisplays(builder.innerDisplays);
    }

    public static final class Builder {
        private long id;
        private String Name;
        private LocalDateTime PubDate;
        private String State;
        private Set<InnerDisplayEntity> innerDisplays;

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

        public Builder PubDate(LocalDateTime val) {
            PubDate = val;
            return this;
        }

        public Builder State(String val) {
            State = val;
            return this;
        }

        public Builder innerDisplays(Set<InnerDisplayEntity> val) {
            innerDisplays = val;
            return this;
        }

        public GuidanceSignEntity build() {
            return new GuidanceSignEntity(this);
        }
    }
}