package com.dynacore.livemap.guidancesign;

import com.dynacore.livemap.core.service.ServiceConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("guidancesign")
public class GuidanceSignConfiguration extends ServiceConfiguration {
}
