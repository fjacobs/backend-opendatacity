package com.dynacore.livemap.guidancesign.service;

import com.dynacore.livemap.core.model.TrafficDTO;
import java.time.OffsetDateTime;

public record DisplayDTO(String id,
                         String name,
                         OffsetDateTime pubDate,
                         Boolean removed,
                         String state,
                         String description,
                         String output,
                         String outputDescription
) implements TrafficDTO {

}



