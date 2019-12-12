package com.dynacore.livemap.traveltime;

import com.dynacore.livemap.Application;
import io.rsocket.*;
import io.rsocket.frame.decoder.PayloadDecoder;
import io.rsocket.transport.netty.client.TcpClientTransport;
import io.rsocket.transport.netty.server.TcpServerTransport;
import io.rsocket.util.DefaultPayload;
import org.geojson.Feature;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.messaging.rsocket.MetadataExtractor;
import org.springframework.messaging.rsocket.RSocketRequester;
import static org.assertj.core.api.Assertions.*;

import com.dynacore.livemap.traveltime.repo.TravelTimeRepo;

import io.rsocket.transport.netty.client.WebsocketClientTransport;
import org.springframework.util.MimeTypeUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Hooks;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;
import reactor.test.StepVerifier;

import java.time.Duration;


@SpringBootTest( properties = "spring.main.web-application-type=reactive",
                 webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class RSocketIntegrationTest {

    @Value("${spring.rsocket.server.port}")
    private int serverPort;

    @Autowired
    private RSocketRequester.Builder builder;

    @Autowired
    TravelTimeRepo repo;

    //@Test
    public void expectFullCollectionOnFirstRequest() throws InterruptedException {

        Hooks.onOperatorDebug();
        RSocketRequester requester =  builder.connect( WebsocketClientTransport
                                             .create(serverPort))
                                             .block();

        requester.route("traveltime-message")
                .retrieveFlux(Feature.class)
                .as(StepVerifier::create)
                .expectNextCount(10)
                .expectNextCount(10)
                .expectNextCount(10)
                .expectNextCount(10)
                .expectNextCount(10)

                .thenCancel()
                .verify();
    }

    @Test
    public void receiveStream() {
        RSocketRequester requester =  builder.connect( WebsocketClientTransport
                .create(serverPort))
                .block();

        requester.route("traveltime-message")
                .retrieveFlux(Feature.class)
                .log()
                .doOnNext(feature -> System.out.println(feature.getId()))
                .blockLast();


    }




}
