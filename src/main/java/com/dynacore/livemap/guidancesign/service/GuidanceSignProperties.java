package com.dynacore.livemap.guidancesign.service;

import com.dynacore.livemap.core.service.ServiceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Profile("guidancesign")
@Configuration
@ConfigurationProperties("guidancesign")
public class GuidanceSignProperties extends ServiceProperties {}
