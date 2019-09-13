package com.dynacore.livemap.common.service;

import lombok.Getter;
import lombok.Setter;


@Getter @Setter
public class GeoJsonRequestConfiguration {
    private String url;
    private int initialDelay;
    private int requestInterval;
}
