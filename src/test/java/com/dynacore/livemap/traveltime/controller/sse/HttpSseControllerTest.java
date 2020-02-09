package com.dynacore.livemap.traveltime.controller.sse;

import com.dynacore.livemap.traveltime.controller.HttpSseController;
import com.dynacore.livemap.traveltime.domain.TravelTimeFeature;
import org.geojson.Feature;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.dynacore.livemap.traveltime.service.TravelTimeService;

import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

public class HttpSseControllerTest {

  @Test
  public void testFeatureSubscription() {
//
//    TravelTimeFeature feature1 = new TravelTimeFeature();
//    feature1.setId("Feature1");
//    feature1.setProperty("prop1", "value1");
//    feature1.setProperty("prop2", 2);
//    TravelTimeFeature feature2 = new TravelTimeFeature();
//    feature2.setId("Feature2");
//    feature2.setProperty("prop2_1", "value2");
//    feature2.setProperty("prop2_2", 3);
//
//    TravelTimeService service = Mockito.mock(TravelTimeService.class);
//    Mockito.when(service.getLiveData()).thenReturn(Flux.just(feature1, feature2));
//
//    ParameterizedTypeReference<ServerSentEvent<Feature>> typeRef =
//        new ParameterizedTypeReference<>() {};
//    WebTestClient.bindToController(new HttpSseController(service))
//        .build()
//        .get()
//        .uri("/featureSubscription")
//        .accept(MediaType.TEXT_EVENT_STREAM)
//        .exchange()
//        .expectStatus()
//        .isOk()
//        .returnResult(typeRef)
//        .getResponseBody()
//        .as(StepVerifier::create)
//        .thenCancel()
//        .verify();
  }
//
//  @Test
//  public void testFeatureCollection() {
//
//    TrafficFeature feature1 = new TrafficFeature();
//    feature1.setId("Feature1");
//    feature1.setProperty("prop1", "value1");
//    feature1.setProperty("prop2", 2);
//    TrafficFeature feature2 = new TrafficFeature();
//    feature2.setId("Feature2");
//    feature2.setProperty("prop3", "value3");
//    feature2.setProperty("prop4", 4);
//    FeatureCollection fc = new FeatureCollection();
//    fc.setFeatures(Arrays.asList(feature1, feature2));
//
//    TravelTimeReactorService service = Mockito.mock(TravelTimeReactorService.class);
//    WebTestClient client = WebTestClient.bindToController(new HttpSseController(service)).build();
//    ParameterizedTypeReference<ServerSentEvent<org.geojson.FeatureCollection>> typeRef =
//        new ParameterizedTypeReference<>() {};
//
//    Mockito.when(service.getFeatureCollection()).thenReturn(Mono.just(fc));
//
//    client
//        .get()
//        .uri("/roadSubscription")
//        .accept(MediaType.TEXT_EVENT_STREAM)
//        .exchange()
//        .expectStatus()
//        .isOk()
//        .returnResult(typeRef)
//        .getResponseBody()
//        .as(StepVerifier::create)
//        .thenAwait(Duration.ofSeconds(1))
//        .expectNextMatches(
//            sse -> {
//              assert sse.data() != null;
//              return sse.data().getFeatures().size() == 2;
//            })
//        .thenCancel()
//        .verify();
//  }
}
