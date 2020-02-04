package com.dynacore.livemap.block.guidancesign.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Immutable;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Immutable
@Table(name = "GUIDANCE_DISPLAY")
@NoArgsConstructor
@Getter
@Setter
public class InnerDisplayEntity {

  @Id
  @Column(name = "ID", nullable = false, updatable = false)
  private UUID innerDisplayId;

  @ManyToOne
  @JoinColumns({
    @JoinColumn(name = "GUIDANCESIGN_ID", referencedColumnName = "ID"),
    @JoinColumn(name = "PUB_DATE", referencedColumnName = "PUB_DATE")
  })
  private GuidanceSignEntity parentRef;

  private String outputDescription;
  private String description;
  private String type;
  private String output;

  private InnerDisplayEntity(Builder builder) {
    setInnerDisplayId(builder.innerDisplayId);
    setParentRef(builder.parentRef);
    setOutputDescription(builder.outputDescription);
    setDescription(builder.description);
    setType(builder.type);
    setOutput(builder.output);
  }

  static final class Builder {
    private GuidanceSignEntity parentRef;
    private UUID innerDisplayId;
    private String outputDescription;
    private String description;
    private String type;
    private String output;

    Builder() {}

    Builder innerDisplayId(UUID val) {
      innerDisplayId = val;
      return this;
    }

    Builder guidanceSignEntity(GuidanceSignEntity val) {
      parentRef = val;
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
