package com.dynacore.livemap.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebclientConfig {

  private static final String SOURCEURL =
      "http://web.redant.net/~amsterdam/ndw/data/reistijdenAmsterdam.geojson";

  @Bean
  public WebClient createWebclient(final HttpClientFactoryConfig httpClientFactoryConfig) {

    ReactorClientHttpConnector httpConnector =
        new ReactorClientHttpConnector(httpClientFactoryConfig.autoConfigHttpClient(SOURCEURL));
    return WebClient.builder()
        .clientConnector(httpConnector)
        .exchangeStrategies(
            ExchangeStrategies.builder()
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(1000 * 5000))
                .build())
        .baseUrl(SOURCEURL)
        .build();
  }
}
