package com.dynacore.livemap.traveltime;

import com.dynacore.livemap.traveltime.domain.TravelTimeMapDTO;
import com.dynacore.livemap.traveltime.domain.TravelTimeFeatureImpl;
import io.rsocket.transport.netty.client.WebsocketClientTransport;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.env.Environment;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.test.context.ActiveProfiles;
import reactor.core.publisher.Hooks;
import reactor.test.StepVerifier;

import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

@SpringBootTest(
    properties = {
      "spring.main.web-application-type=reactive",
      "traveltime.requestinterval: 600ms",
      "traveltime.elementdelay: 0s"
    },
    webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ActiveProfiles("test")
class RSocketIntegrationTest {

  @Value("${spring.rsocket.server.port}")
  private int serverPort;

  @Autowired private RSocketRequester.Builder builder;

  @Autowired ApplicationContext applicationContext;

  @Autowired private Environment environment;

  @Test
  public void testLiveStream() {
    Hooks.onOperatorDebug();
    RSocketRequester webSocket =
        builder.connect(WebsocketClientTransport.create(serverPort)).block();
    System.out.println(Arrays.asList(applicationContext.getBeanDefinitionNames()));
    System.out.println(Arrays.asList(this.environment.getActiveProfiles()));

    webSocket
        .route("TRAVELTIME_STREAM")
        .retrieveFlux(TravelTimeFeatureImpl.class)
        .as(StepVerifier::create)
        .expectSubscription()
        .expectNextMatches(
            feature -> feature.getPubDate().isEqual(OffsetDateTime.parse("2019-10-16T16:52:00Z")))
        .expectNextCount(4)
        .expectNextMatches(
            feature -> feature.getPubDate().isEqual(OffsetDateTime.parse("2019-10-16T17:00:00Z")))
        .expectNextCount(4)
        .thenCancel()
        .verify();
  }

  @Test
  public void testReplayAll() {

    //    46	id1	name1	2019-10-16 17:00:00.000000	2020-02-08 16:46:09.555311	H	623	-1	-1
    //    38	id1	name1	2019-10-16 18:00:00.000000	2020-02-08 16:46:03.533541	H	623	-1	-1
    //    41	id1	name1	2019-10-16 16:52:00.000000	2020-02-08 16:46:06.550061	H	623	-1	-1
    //    44	id2	name2	2019-10-16 16:52:00.000000	2020-02-08 16:46:06.550463	H	627	12000	11
    //    47	id2	name2	2019-10-16 17:00:00.000000	2020-02-08 16:46:09.555625	H	627	20	22
    //    39	id2	name2	2019-10-16 18:00:00.000000	2020-02-08 16:46:03.535244	H	627	33	33
    //    42	id3	name3	2019-10-16 16:52:00.000000	2020-02-08 16:46:06.550641	H	458	-1	-1
    //    48	id3	name3	2019-10-16 17:00:00.000000	2020-02-08 16:46:09.555816	H	458	-1	-1
    //    36	id3	name3	2019-10-16 18:00:00.000000	2020-02-08 16:46:03.535482	H	458	-1	-1
    //    50	id4	name4	2019-10-16 17:00:00.000000	2020-02-08 16:46:09.555979	H	691	1200	88
    //    37	id4	name4	2019-10-16 18:00:00.000000	2020-02-08 16:46:03.546795	H	691	44	88
    //    45	id4	name4	2019-10-16 16:52:00.000000	2020-02-08 16:46:06.551507	H	691	1200	88
    //    43	id5	name5	2019-10-16 16:52:00.000000	2020-02-08 16:46:06.551719	H	598	120	24
    //    49	id5	name5	2019-10-16 17:00:00.000000	2020-02-08 16:46:09.556229	H	598	120	24
    //    40	id5	name5	2019-10-16 18:00:00.000000	2020-02-08 16:46:03.547304	H	598	120	24

    //    Amount of features changed: 5 ->  all       ->    16:52:00.000000
    //    Amount of features changed: 1 -> id2        ->    17:00:00
    //    Amount of features changed: 2  -> id2 + id4 ->    18:00:00

    Hooks.onOperatorDebug();
    RSocketRequester webSocket =
        builder.connect(WebsocketClientTransport.create(serverPort)).block();
    ParameterizedTypeReference<List<TravelTimeMapDTO>> typeRef = new ParameterizedTypeReference<>() {};

    StepVerifier.create(webSocket.route("TRAVELTIME_REPLAY").data(10).retrieveFlux(typeRef))
        .expectSubscription()
        .consumeNextWith(group -> assertThat(group.size(), is(5)))
        .consumeNextWith(
            group -> {
              assertThat(group.size(), is(1));
              var dto2 =
                  group.stream().filter(dto -> dto.getId().equals("id2")).findAny().orElseThrow();
              assertThat(dto2.getVelocity(), is(20));
              assertTrue(dto2.getPubDate().isEqual(OffsetDateTime.parse("2019-10-16T17:00:00Z")));
            })
        .consumeNextWith(
            group -> {
              assertThat(group.size(), is(2));
              var dto2 =
                  group.stream().filter(dto -> dto.getId().equals("id2")).findAny().orElseThrow();
              assertTrue(dto2.getPubDate().isEqual(OffsetDateTime.parse("2019-10-16T18:00:00Z")));
              assertThat(dto2.getVelocity(), is(33));
            })
        .verifyComplete();
  }
}
