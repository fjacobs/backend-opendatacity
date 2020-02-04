package com.dynacore.livemap.parking.service;

import com.dynacore.livemap.core.service.ServiceConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Profile("parking")
@Configuration
@ConfigurationProperties("parking")
public class ParkingServiceConfig extends ServiceConfiguration {}

