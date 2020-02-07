package com.dynacore.livemap.parking.domain;

import com.dynacore.livemap.core.model.TrafficDTO;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@NoArgsConstructor
public class ParkingDTO extends TrafficDTO {

  public ParkingDTO(String id, OffsetDateTime pubDate) {
    setId(id);
    setPubDate(pubDate);
  }

}
