package com.dynacore.livemap.guidancesign.entity;

import com.dynacore.livemap.guidancesign.model.GuidanceSignModel;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;


@Entity
@Table(name = "GUIDANCE_SIGN")
@Getter @Setter
public class GuidanceSignEntity implements Serializable {

    @Transient
    Logger logger = LoggerFactory.getLogger(GuidanceSignEntity.class);

    @Id
    @Column(name = "ID", nullable = false, updatable = false)
    private UUID guidanceSignId;

    //Flatted from model.properties:
    private String name;
    @Id
    @Column(name = "PUB_DATE")
    private LocalDateTime pubDate;
    private String state;

    @OneToMany(mappedBy = "parentRef", fetch = FetchType.LAZY)
    private Set<InnerDisplayEntity> innerDisplays;

    public GuidanceSignEntity() {
    }

    public GuidanceSignEntity(GuidanceSignModel model) {
        try {
            guidanceSignId = model.getId();
            name = model.getName();
            pubDate = model.getPubDate();
            state = model.getState();
            innerDisplays = model.getProperties().getInnerDisplayModelList().stream()
                    .map(inner -> new InnerDisplayEntity.Builder()
                            .innerDisplayId(inner.getId())
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GuidanceSignEntity)) return false;
        GuidanceSignEntity that = (GuidanceSignEntity) o;
        return guidanceSignId.equals(that.guidanceSignId) &&
                name.equals(that.name) &&
                pubDate.equals(that.pubDate) &&
                state.equals(that.state) &&
                innerDisplays.equals(that.innerDisplays);
    }

    @Override
    public int hashCode() {
        return Objects.hash(guidanceSignId, name, pubDate, state, innerDisplays);
    }
}