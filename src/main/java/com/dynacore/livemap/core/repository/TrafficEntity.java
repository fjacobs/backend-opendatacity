package com.dynacore.livemap.core.repository;

import lombok.*;
import org.springframework.data.annotation.Id;

import java.time.OffsetDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public abstract class TrafficEntity implements TrafficEntityInterface {

  @Id protected Integer pkey;

  protected String id;
  protected String name;

  protected OffsetDateTime pubDate;
  protected OffsetDateTime ourRetrieval;

}
