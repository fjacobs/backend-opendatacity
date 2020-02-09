package com.dynacore.livemap.guidancesign.service;

import com.dynacore.livemap.core.service.ServiceConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Profile("guidancesign")
@Configuration
@ConfigurationProperties("guidancesign")
public class GuidanceSignServiceConfig extends ServiceConfiguration {}
