package com.dynacore.livemap.traveltime.repo;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@NoArgsConstructor
@Data
public class ReplayMetaData {
    Integer count;
    OffsetDateTime pubDate;
}
