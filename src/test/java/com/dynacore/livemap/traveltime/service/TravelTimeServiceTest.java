package com.dynacore.livemap.traveltime.service;

import com.dynacore.livemap.traveltime.repo.TravelTimeRepo;
import okhttp3.HttpUrl;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.geojson.Feature;
import org.geojson.FeatureCollection;

import org.junit.Assert;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Hooks;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.io.*;
import java.time.Duration;

import static org.mockito.Mockito.mock;

class TravelTimeServiceTest {

    static String jsonCorrect = "{\"type\":\"FeatureCollection\",\"crs\":{\"type\":\"name\",\"properties\":{\"name\":\"urn:ogc:def:crs:OGC:1.3:CRS84\"}}," +
            "\"features\":[{\"type\":\"Feature\",\"properties\":{\"Id\":\"RWS01_MONIBAS_0091hrl0356ra0\",\"" +
            "Name\":\"0091hrl0356ra0\",\"Type\":\"H\",\"Timestamp\":\"2019-10-16T15:52:00Z\",\"Length\":623,\"" +
            "Velocity\":0},\"geometry\":{\"type\":\"LineString\",\"coordinates\":[[4.776645712715283,52.338380232953895]" +
            ",[4.776853788479121,52.33827956952142],[4.77842037340242,52.337460757548655],[4.778671519814815,52.33733621949967]" +
            ",[4.780652279562285,52.336267847567214],[4.782159793662865,52.33546665913931],[4.782751047173977,52.335146118375064],[4.78306134179851,52.33498592177948],[4.78356224185475,52.33472011500613]]}}" +
            ",{\"type\":\"Feature\",\"properties\":{\"Id\":\"RWS01_MONIBAS_0051hrl0092rb0\",\"Name\":\"0051hrl0092rb0\",\"Type\":\"H\",\"Timestamp\":\"2019-10-16T15:52:00Z\",\"Length\":627,\"Traveltime\":22,\"" +
            "Velocity\":0},\"geometry\":{\"type\":\"LineString\",\"coordinates\":[[4.747882941552497,52.36389974515355]," +
            "[4.747441699702729,52.36372959463498],[4.747144894615104,52.36352614166333],[4.74667824897942,52.36325203253315],[4.745704027145298,52.36263386826604]," +
            "[4.744950288694216,52.36210864000742],[4.744384634980363,52.361635811770356],[4.743800919091215,52.36117021663649],[4.743137406016038,52.36062712270663]," +
            "[4.74267909704837,52.36019523441274],[4.742046603115947,52.359578948936196]]}},{\"type\":\"Feature\",\"properties\":{\"Id\":\"RWS01_MONIBAS_0091hrl0059ra1\"," +
            "\"Name\":\"0091hrl0059ra1\",\"Type\":\"H\",\"Timestamp\":\"2019-10-16T15:52:00Z\",\"Length\":458,\"Velocity\":0},\"geometry\":{\"type\":\"LineString\",\"coordinates\"" +
            ":[[5.004879014653774,52.324294987008926],[5.005005993142421,52.324559388367234],[5.005049301921006,52.32465911236145],[5.005433357558088,52.325807142107685]," +
            "[5.005562368354391,52.32628371039965],[5.005723592066905,52.32730043716067],[5.005711772825435,52.327993424971154],[5.005687110573994,52.32836111625229]]}}]}";

    static String notAjson = "gjhkg^&#fsa@^&@*( ";


    static MockWebServer server;
    static HttpUrl baseUrl;
    static TravelTimeService service;
    static ServiceConfig serviceConfig;

    @BeforeAll
    static void setUp() throws IOException {

        server = new MockWebServer();
        baseUrl = server.url("/v1/chat/");
        TravelTimeRepo repo = mock(TravelTimeRepo.class);
        WebClient webClient = WebClient.create(baseUrl.url().toString());

        serviceConfig = new ServiceConfig();;
        serviceConfig.setInitialDelay(0);
        serviceConfig.setRequestInterval(1);
        serviceConfig.setUrl(baseUrl.url().toString());

        service = new TravelTimeService(repo,webClient,serviceConfig);
    }

    @Test
    void getCorrectJsonThenBrokenJson() throws InterruptedException {
        Hooks.onOperatorDebug();
        server.enqueue(
                new MockResponse()
                    .setResponseCode(200)
                    .setBody(jsonCorrect));

        service.getFeatures()
                .as(StepVerifier::create)
                .expectNextCount(3)
                .verifyComplete();

        server.takeRequest().getHeaders();

        server.enqueue(
                new MockResponse()
                        .setResponseCode(200)
                        .setBody(notAjson));

        service.getFeatures()
                .delaySubscription(Duration.ofSeconds(serviceConfig.getRequestInterval()+1))
                .as(StepVerifier::create)
                .expectErrorMatches(throwable -> throwable instanceof ClassCastException &&
                        throwable.getMessage().equals("Cannot cast reactor.core.publisher.MonoCallableOnAssembly to org.geojson.FeatureCollection")
                )
                .verify();
    }


    //Same as getFeatures but just converts to FeatureCollection
    @Test
    void getFeatureCollection() {

        Feature feature1 = new Feature();
        feature1.setId("Feature1");
        feature1.setProperty("prop1", "value1");
        feature1.setProperty("prop2", 2);
        Feature feature2 = new Feature();
        feature2.setId("Feature2");
        feature2.setProperty("prop2_1", "value2");
        feature2.setProperty("prop2_2", 3);

        Mono<FeatureCollection> fcMono =service.getFeatureCollection(Flux.just(feature1,feature2));

        fcMono.as(StepVerifier::create)
                .assertNext(fc-> {
                    Assert.assertEquals("Feature2", fc.getFeatures().get(1).getId());
                    Assert.assertEquals(3, fc.getFeatures().get(1).getProperties().get("prop2_2"));

                })
                .verifyComplete();
    }

    @AfterAll
    static void tearDown() throws IOException {
        server.shutdown();
    }


}