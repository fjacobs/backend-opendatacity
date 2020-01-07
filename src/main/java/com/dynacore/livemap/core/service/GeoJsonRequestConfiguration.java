package com.dynacore.livemap.core.service;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Duration;

@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
public class GeoJsonRequestConfiguration {
    private String url;
    private Duration initialDelay;
    private Duration requestInterval;
    private Duration elementDelay;
    private Boolean dbEnabled;
}
