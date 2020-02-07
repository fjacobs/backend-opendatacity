package com.dynacore.livemap.parking.repo;

import com.dynacore.livemap.core.repository.TrafficEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.OffsetDateTime;
import java.util.Objects;

@Table
@Getter
@Setter
@NoArgsConstructor
@ToString
public class ParkingEntity extends TrafficEntity {

  private static final Logger log = LoggerFactory.getLogger(ParkingEntity.class);

  @Id private Integer pkey;

  private String id;
  private String name;

  private OffsetDateTime pubDate;
  private OffsetDateTime retrievedFromThirdParty;
  private String type;


}
