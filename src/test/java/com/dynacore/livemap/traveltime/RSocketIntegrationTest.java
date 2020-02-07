package com.dynacore.livemap.traveltime;

import com.dynacore.livemap.configuration.adapter.FileToGeojson;
import com.dynacore.livemap.core.adapter.GeoJsonAdapter;
import com.dynacore.livemap.core.model.TrafficDTO;
import com.dynacore.livemap.core.model.TrafficFeature;
import com.dynacore.livemap.traveltime.domain.TravelTimeDTO;
import com.dynacore.livemap.traveltime.service.TravelTimeServiceConfig;
import io.rsocket.transport.netty.client.WebsocketClientTransport;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.env.Environment;
import org.springframework.messaging.rsocket.RSocketRequester;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Hooks;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

@SpringBootTest(
    properties = "spring.main.web-application-type=reactive",
    webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class RSocketIntegrationTest {

  @Value("${spring.rsocket.server.port}")
  private int serverPort;

  @Autowired private RSocketRequester.Builder builder;

  @Autowired ApplicationContext applicationContext;

  @Autowired private Environment environment;

  @Test
  public void testLiveStream() {
    Hooks.onOperatorDebug();
//    RSocketRequester webSocket =
//        builder.connect(WebsocketClientTransport.create(serverPort)).block();
    System.out.println(Arrays.asList(applicationContext.getBeanDefinitionNames()));
    System.out.println(Arrays.asList( this.environment.getActiveProfiles()));


    //    webSocket
    //        .route("TRAVELTIME_STREAM")
    //        .retrieveFlux(TrafficFeature.class)
    //        .as(StepVerifier::create)
    //        .expectNextCount(1011)
    //        .expectNextMatches(feature -> feature.getProperties().get("Velocity").equals(50))
    //        //     //   .expectNextCount(1011)
    //        //        .expectNextMatches(feature ->
    // feature.getProperties().get("Velocity").equals(120))
    //        //      //  .expectNextCount(1011)
    //        //        .expectNextMatches(feature ->
    // feature.getProperties().get("Velocity").equals(50))
    //        //       // .expectNextCount(1011)
    //        //        .expectNextMatches(feature ->
    // feature.getProperties().get("Velocity").equals(120))
    //        .thenCancel()
    //        .verify();
  }

  @Test
  public void simpleTest() {
    Hooks.onOperatorDebug();
    RSocketRequester webSocket =
        builder.connect(WebsocketClientTransport.create(serverPort)).block();
    ParameterizedTypeReference<List<TrafficDTO>> typeRef = new ParameterizedTypeReference<>() {};
    webSocket
        .route("TRAVELTIME_REPLAY")
        .data(1)
        .retrieveFlux(typeRef)
        //    .doOnNext(System.out::println)
        .blockLast();
  }

  @Test
  void testReplayAllWithVirtualTime() {
    Hooks.onOperatorDebug();
    RSocketRequester webSocket =
        builder.connect(WebsocketClientTransport.create(serverPort)).block();

    ParameterizedTypeReference<List<TravelTimeDTO>> typeRef = new ParameterizedTypeReference<>() {};

    OffsetDateTime pubDate1 = OffsetDateTime.parse("2020-01-21T09:56Z");
    OffsetDateTime pubDate2 = OffsetDateTime.parse("2020-01-21T10:00:01Z");

    StepVerifier.withVirtualTime(
            () -> webSocket.route("TRAVELTIME_REPLAY").data(1).retrieveFlux(typeRef).take(2))
        .expectSubscription()
        .expectNoEvent(Duration.ofSeconds(1))
        .consumeNextWith(
            group -> {
              assertThat(group.size(), is(1012));
              assertThat(group.get(0).getPubDate(), is(pubDate1));
            })
        .thenAwait(Duration.ofSeconds(1))
        .consumeNextWith(
            group -> {
              assertThat(group.size(), is(153));
              assertThat(group.get(0).getPubDate(), is(pubDate2));
            })
        .verifyComplete();
  }

  @TestConfiguration
  static class FileReaderH2Beans {

    @Profile("filetest")
    @Bean
    @Primary
    TravelTimeServiceConfig createServiceConfig() {
      TravelTimeServiceConfig serviceConfig = new TravelTimeServiceConfig();
      serviceConfig.setInitialDelay(Duration.ZERO);
      serviceConfig.setRequestInterval(Duration.ofSeconds(2));
      serviceConfig.setElementDelay(Duration.ofMillis(2));
      serviceConfig.setSaveToDbEnabled(false);
      return serviceConfig;
    }

    @Profile("filetest")
    @Bean(name = "fileReaderRepeat")
    @Primary
    GeoJsonAdapter fileReaderRepeat() {
      return (interval) ->
          Flux.fromIterable(FileToGeojson.readCollection("/traveltimedata/blinkingsmall/"))
              .delayElements(interval)
              .repeat(10);
    }
  }
}
