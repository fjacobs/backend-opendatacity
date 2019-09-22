package com.dynacore.livemap.traveltime;

import com.dynacore.livemap.common.service.GeoJsonRequestConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("traveltime")
public class TravelTimeConfiguration extends GeoJsonRequestConfiguration {

}
