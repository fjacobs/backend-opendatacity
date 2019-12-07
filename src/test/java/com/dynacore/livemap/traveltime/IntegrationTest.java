package com.dynacore.livemap.traveltime;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;

import com.dynacore.livemap.core.http.HttpClientFactory;
import org.geojson.Feature;
import org.junit.Assert;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.r2dbc.core.DatabaseClient;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.util.ResourceUtils;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.HttpUrl;

import com.dynacore.livemap.testing.database.H2TestSupport;
import com.dynacore.livemap.traveltime.controller.TravelTimeController;
import com.dynacore.livemap.traveltime.repo.TravelTimeRepo;
import com.dynacore.livemap.traveltime.service.ServiceConfig;
import com.dynacore.livemap.traveltime.service.TravelTimeService;

class IntegrationTest {

    static String json1;
    static String json2;
    static String json3;
    static String notAjson = "this is not a json";

    static TravelTimeController controller;
    static MockWebServer server;

    @BeforeAll
    static void setUp() {

        server = new MockWebServer();
        HttpUrl baseUrl = server.url("/v1/chat/");

        ServiceConfig serviceConfig = new ServiceConfig();
        serviceConfig.setInitialDelay(0);
        serviceConfig.setRequestInterval(1);
        serviceConfig.setUrl(baseUrl.url().toString());
        DatabaseClient client = DatabaseClient.create(H2TestSupport.createConnectionFactory());

        client.execute(H2TestSupport.CREATE_TABLE_TRAVEL_TIME)
              .then()
              .block();

        TravelTimeRepo repo = new TravelTimeRepo(client);

        ReactorClientHttpConnector httpConnector = new ReactorClientHttpConnector(new HttpClientFactory().autoConfigHttpClient(baseUrl.toString()));
        WebClient webClient = WebClient.builder()
                .clientConnector(httpConnector)
                .exchangeStrategies(ExchangeStrategies.builder()
                        .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(1024 * 5000))
                        .build())
                .baseUrl(baseUrl.toString())
                .build();


        TravelTimeService service = new TravelTimeService(repo, webClient, serviceConfig);
        controller = new TravelTimeController(service);
    }

    @Test
    public void fileReadTest() throws FileNotFoundException {
    }

    private String getString(String fileName)  {
        String featureCollection = "";
        try {
            Path path = ResourceUtils.getFile(this.getClass().getResource("/" + fileName)).toPath();
            try {
                Charset charset = StandardCharsets.UTF_8;
                try (BufferedReader reader = Files.newBufferedReader(path, charset)) {
                    String tempLine;
                    while ((tempLine = reader.readLine()) != null) {
                        featureCollection = "" + tempLine;
                    }
                } catch (IOException x) { throw x;}
            } catch (Exception e) { throw e;}
        } catch (Exception error) {
            error.printStackTrace();
            Assert.fail("Error reading test geojson file '" +  fileName + "'  in test/resources: " + error.toString());
        }
        return featureCollection;
    }

    @Test
    void expectFullCollectionOnFirstRequest() {

        json1 = getString("fc1.geojson");
        json2 = getString("fc2.geojson");
        json3 = getString("fc3.geojson");

        server.enqueue(
                new MockResponse()
                        .setResponseCode(200)
                        .setBody(json1));

        WebTestClient client = WebTestClient.bindToController(controller).build();

        ParameterizedTypeReference<ServerSentEvent<Feature>> typeRef = new ParameterizedTypeReference<>() {};
        var featureEvent = client.get()
                .uri("/featureSubscription")
                .accept(MediaType.TEXT_EVENT_STREAM)
                .exchange()
                .expectStatus().isOk()
                .returnResult(typeRef);

        Flux<ServerSentEvent<Feature>> eventFlux = featureEvent.getResponseBody();

        StepVerifier.create(eventFlux)
                .thenAwait(Duration.ofSeconds(1))
                .expectNextCount(128)
                .expectComplete()
                .verify();
    }

    @AfterAll
    static void tearDown() throws IOException {
        server.shutdown();
    }
}
