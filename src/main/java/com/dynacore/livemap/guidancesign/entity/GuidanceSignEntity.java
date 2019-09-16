package com.dynacore.livemap.guidancesign.entity;

import com.dynacore.livemap.guidancesign.model.GuidanceSignModel;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;


@Entity
@Table(name = "GUIDANCE_SIGN")
@Getter
@Setter
public class GuidanceSignEntity {

    @Transient
    Logger logger = LoggerFactory.getLogger(GuidanceSignEntity.class);

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "guidancesign_id")
    private long id;

    //Flatted from model.properties:
    private String name;
    private LocalDateTime pubDate;
    private String state;

    @OneToMany(mappedBy = "guidanceSignEntity")
    private Set<InnerDisplayEntity> innerDisplays;

    public GuidanceSignEntity() {
    }

    public GuidanceSignEntity(GuidanceSignModel model) {
        try {
            name = model.getName();
            pubDate = model.getPubDate();
            state = model.getState();
            innerDisplays = model.getProperties().getInnerDisplayModelList().stream()
                    .map(inner -> new InnerDisplayEntity.Builder()
                            .outputDescription(inner.getOutputDescription())
                            .output(inner.getOutput())
                            .type(inner.getType())
                            .description(inner.getDescription())
                            .guidanceSignEntity(this)
                            .build())
                    .collect(Collectors.toSet());
        } catch (Exception e) {
            logger.error("GuidanceSignDTO construction error: ", e);
        }
    }

    public GuidanceSignEntity(String name, LocalDateTime pubDate, String state, Set<InnerDisplayEntity> innerDisplays) {
        this.name = name;
        this.pubDate = pubDate;
        this.state = state;
        this.innerDisplays = innerDisplays;
    }

    private GuidanceSignEntity(Builder builder) {
        setId(builder.id);
        setName(builder.name);
        setPubDate(builder.pubDate);
        setState(builder.state);
        setInnerDisplays(builder.innerDisplays);
    }

    public static final class Builder {
        private long id;
        private String name;
        private LocalDateTime pubDate;
        private String state;
        private Set<InnerDisplayEntity> innerDisplays;

        public Builder() {
        }

        public Builder id(long val) {
            id = val;
            return this;
        }

        public Builder name(String val) {
            name = val;
            return this;
        }

        public Builder pubDate(LocalDateTime val) {
            pubDate = val;
            return this;
        }

        public Builder state(String val) {
            state = val;
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