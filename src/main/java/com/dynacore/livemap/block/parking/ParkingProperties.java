package com.dynacore.livemap.block.parking;

import com.dynacore.livemap.core.service.ServiceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("parking")
public class ParkingProperties extends ServiceProperties {}
