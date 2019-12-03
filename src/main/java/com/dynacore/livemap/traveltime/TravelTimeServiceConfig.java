package com.dynacore.livemap.traveltime;

import com.dynacore.livemap.common.service.GeoJsonRequestConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Profile("traveltime")
@Configuration
@ConfigurationProperties("traveltime")
public class TravelTimeServiceConfig extends GeoJsonRequestConfiguration {}
