package com.dynacore.livemap.traveltime;

import org.geojson.Feature;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.messaging.rsocket.RSocketRequester;

import com.dynacore.livemap.traveltime.repo.TravelTimeRepo;

import io.rsocket.transport.netty.client.WebsocketClientTransport;
import reactor.core.publisher.Hooks;
import reactor.test.StepVerifier;


@SpringBootTest( properties = "spring.main.web-application-type=reactive",
                 webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class RSocketIntegrationTest {

    @Value("${spring.rsocket.server.port}")
    private int serverPort;

    @Autowired
    private RSocketRequester.Builder builder;

    @Autowired
    TravelTimeRepo repo;

    @Test
    void expectFullCollectionOnFirstRequest() throws InterruptedException {
        Hooks.onOperatorDebug();
        RSocketRequester requester =  builder.connect( WebsocketClientTransport
                                             .create(serverPort))
                                             .block();

        requester.route("traveltime-message")
                .retrieveFlux(Feature.class)
                .as(StepVerifier::create)
                .expectNextCount(1012)
                .thenCancel()
                .verify();

    }
}
