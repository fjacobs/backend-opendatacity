package com.dynacore.livemap.core.service;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
public class GeoJsonRequestConfiguration {
    private String url;
    private int initialDelay;
    private int requestInterval;
}
