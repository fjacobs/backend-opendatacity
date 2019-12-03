package com.dynacore.livemap.traveltime;

import com.dynacore.livemap.common.service.GeoJsonRequestConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

@Profile("traveltime")
@Configuration
@ConfigurationProperties("traveltime")
public class ServiceConfig extends GeoJsonRequestConfiguration {

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }
}

