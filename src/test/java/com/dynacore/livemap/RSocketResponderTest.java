// package com.dynacore.livemap;
//
// import java.time.Duration;
// import java.time.OffsetDateTime;
//
// import com.dynacore.livemap.traveltime.repo.TravelTimeEntity;
// import lombok.AllArgsConstructor;
// import lombok.ToString;
// import org.junit.jupiter.api.AfterAll;
// import org.junit.jupiter.api.BeforeAll;
// import org.junit.jupiter.api.Test;
//
// import io.rsocket.RSocketFactory;
// import io.rsocket.SocketAcceptor;
// import io.rsocket.frame.decoder.PayloadDecoder;
// import io.rsocket.metadata.WellKnownMimeType;
// import io.rsocket.transport.netty.server.CloseableChannel;
// import io.rsocket.transport.netty.server.TcpServerTransport;
//
// import org.springframework.messaging.rsocket.RSocketRequester;
//
// import reactor.test.StepVerifier;
// import org.springframework.messaging.rsocket.RSocketStrategies;
// import reactor.core.publisher.Flux;
// import reactor.core.publisher.Mono;
// import reactor.core.publisher.ReplayProcessor;
//
// import org.springframework.context.annotation.AnnotationConfigApplicationContext;
// import org.springframework.context.annotation.Bean;
// import org.springframework.stereotype.Controller;
// import org.springframework.context.annotation.Configuration;
// import org.springframework.messaging.handler.annotation.MessageMapping;
// import org.springframework.messaging.rsocket.annotation.support.RSocketMessageHandler;
// import org.springframework.util.MimeType;
// import org.springframework.util.MimeTypeUtils;
//
//
// public class RSocketResponderTest {
//
//    private static AnnotationConfigApplicationContext context;
//    private static CloseableChannel server;
//    private static RSocketRequester client;
//
//
//    @BeforeAll
//    @SuppressWarnings("ConstantConditions")
//    public static void setupOnce() {
//
//        MimeType metadataMimeType = MimeTypeUtils.parseMimeType(
//                WellKnownMimeType.MESSAGE_RSOCKET_ROUTING.getString());
//
//        context = new AnnotationConfigApplicationContext(ServerConfig.class);
//        RSocketMessageHandler messageHandler = context.getBean(RSocketMessageHandler.class);
//        SocketAcceptor responder = messageHandler.responder();
//
//        server = RSocketFactory.receive()
//                .frameDecoder(PayloadDecoder.ZERO_COPY)
//                .acceptor(responder)
//                .transport(TcpServerTransport.create("localhost", 9897))
//                .start()
//                .block();
//
//        client = RSocketRequester.builder()
//                .metadataMimeType(metadataMimeType)
//                .rsocketStrategies(context.getBean(RSocketStrategies.class))
//                .connectTcp("localhost", 9897)
//                .block();
//    }
//
//    @AfterAll
//    public static void tearDownOnce() {
//        client.rsocket().dispose();
//        server.dispose();
//    }
//
//    @Test
//    public void requestEntity() {
//        //Client
//        Mono<EntityClass> result  = client.route("JsonTest")
//                            .retrieveMono(EntityClass.class);
//
//        //Server
//        result.doOnNext(System.out::println)
//                .block();
//    }
//
//    @Test
//    public void requestEntity2() {
//        //Client
//        Mono<TravelTimeEntity> result  = client.route("JsonTest2")
//                .retrieveMono(TravelTimeEntity.class);
//
//        //Server
//        result.doOnNext(System.out::println)
//                .block();
//    }
//
//    @Test
//    public void echo() {
//        //Client
//        Flux<String> result = Flux.range(1, 3)
//                .concatMap(i -> client.route("FeatureMetaRequest")
//                        .data("Pubdate request: " + i)
//                        .retrieveMono(String.class));
//
//        //Server
//        StepVerifier.create(result)
//                .expectNext("Server response for: Pubdate request: 1")
//                .expectNext("Server response for: Pubdate request: 2")
//                .expectNext("Server response for: Pubdate request: 3")
//                .expectComplete()
//                .verify(Duration.ofSeconds(5));
//    }
//
//    @Test
//    public void echoStream() {
//        Flux<String> result = client.route("FeatureRequest")
//                .data("Client requesting all Features of date: <date>")
//                .retrieveFlux(String.class);
//
//        StepVerifier.create(result)
//                .expectNextCount(8)
//                .thenCancel()
//                .verify(Duration.ofSeconds(5));
//    }
//
//    @Controller
//    static class ServerController {
//
//        final ReplayProcessor<String> fireForgetPayloads = ReplayProcessor.create();
//
//
//        @MessageMapping("jsonTest2")
//        Mono<TravelTimeEntity> sendJson2() {
//            return Mono.just(new TravelTimeEntity(1, "id", null, null, null, null, 100, null,null
// ));
//        }
//
//
//        @MessageMapping("jsonTest")
//        Mono<EntityClass> sendJson() {
//            return Mono.just(new EntityClass(null, "id", 100, null));
//        }
//
//        @MessageMapping("receive")
//        void receive(String payload) {
//            this.fireForgetPayloads.onNext(payload);
//        }
//
//        @MessageMapping("FeatureMetaRequest")
//        Mono<String> featureMetaRequestHandler(String payload) {
//            return Mono.just("Server response for: " + payload);
//        }
//
//        @MessageMapping("FeatureRequest")
//        Flux<String> echoStream(String payload) {
//            return Flux.interval(Duration.ofMillis(10))
//                    .map(aLong -> "Server response: Feature property for:" + payload);
//        }
//    }
//
//
//    @Configuration
//    static class ServerConfig {
//
//        @Bean
//        public ServerController controller() {
//            return new ServerController();
//        }
//
//        @Bean
//        public RSocketMessageHandler messageHandler() {
//            RSocketMessageHandler handler = new RSocketMessageHandler();
//            handler.setRSocketStrategies(rsocketStrategies());
//            return handler;
//        }
//
//        @Bean
//        public RSocketStrategies rsocketStrategies() {
//            return RSocketStrategies.create();
//        }
//    }
//
// }
//
//
// @AllArgsConstructor
// @ToString
// class EntityClass {
//    Integer pkey;
//
//    String id;
//    Integer travelTime;
//    Integer length;
// }
