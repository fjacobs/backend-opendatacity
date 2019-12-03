package com.dynacore.livemap.traveltime.service;


import com.dynacore.livemap.common.http.HttpClientFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;


@Configuration
public class WebclientConfiguration {

    private static final String SOURCEURL = "http://web.redant.net/~amsterdam/ndw/data/reistijdenAmsterdam.geojson";

    @Bean
    public WebClient createWebclient(final HttpClientFactory httpClientFactory) {

        ReactorClientHttpConnector httpConnector = new ReactorClientHttpConnector(httpClientFactory.autoConfigHttpClient(SOURCEURL));
        return WebClient.builder()
                .clientConnector(httpConnector)
                .baseUrl(SOURCEURL)
                .build();
    }
}
