package com.dynacore.livemap.block.guidancesign;

import com.dynacore.livemap.core.service.ServiceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("guidancesignold")
public class GuidanceSignPropertiesOld extends ServiceProperties {}
