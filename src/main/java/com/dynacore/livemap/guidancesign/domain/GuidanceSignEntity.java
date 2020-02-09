package com.dynacore.livemap.guidancesign.domain;

import com.dynacore.livemap.core.repository.TrafficEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.relational.core.mapping.Table;



@Table
@ToString
@Getter @Setter
public class GuidanceSignEntity extends TrafficEntity {

  private static final Logger log = LoggerFactory.getLogger(GuidanceSignEntity.class);

  public GuidanceSignEntity() { }

  public GuidanceSignEntity(GuidanceSignFeature feature) {

  }
}
