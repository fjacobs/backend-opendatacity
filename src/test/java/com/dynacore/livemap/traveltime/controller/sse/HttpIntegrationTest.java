package com.dynacore.livemap.traveltime.controller.sse;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;

import com.dynacore.livemap.configuration.HttpClientFactoryConfig;
import com.dynacore.livemap.testing.database.PostgresTestSupport;
import com.dynacore.livemap.configuration.adapter.HttpAdapter;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.geojson.Feature;
import org.junit.Assert;

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
import reactor.core.publisher.Hooks;
import reactor.test.StepVerifier;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.HttpUrl;

import com.dynacore.livemap.testing.database.H2TestSupport;
import com.dynacore.livemap.traveltime.controller.HttpSseController;
import com.dynacore.livemap.traveltime.repo.TravelTimeRepo;
import com.dynacore.livemap.traveltime.service.TravelTimeServiceConfig;
import com.dynacore.livemap.traveltime.service.TravelTimeService;

class HttpIntegrationTest {

    static String json1;
    static String json2;
    static String json3;
    static String notAjson = "this is not a json";

    static HttpSseController controller;
    static MockWebServer server;
    static DatabaseClient databaseClient;

    static void setUp() throws JsonProcessingException {

        server = new MockWebServer();

        HttpUrl baseUrl = server.url("/v1/chat/");

        TravelTimeServiceConfig serviceConfig = new TravelTimeServiceConfig();
        serviceConfig.setInitialDelay(Duration.ZERO);
        serviceConfig.setRequestInterval(Duration.ofSeconds(1));
        serviceConfig.setUrl(baseUrl.url().toString());

        databaseClient = DatabaseClient.create(PostgresTestSupport.createConnectionFactory(PostgresTestSupport.database()));

        databaseClient.execute("DROP TABLE IF EXISTS travel_time_entity;")
                .then()
                .block();

        databaseClient.execute(H2TestSupport.CREATE_TABLE_TRAVEL_TIME)
              .then()
              .block();

        TravelTimeRepo repo = new TravelTimeRepo(databaseClient);

        ReactorClientHttpConnector httpConnector = new ReactorClientHttpConnector(new HttpClientFactoryConfig().autoConfigHttpClient(baseUrl.toString()));

        WebClient webClient = WebClient.builder()
                .clientConnector(httpConnector)
                .exchangeStrategies(ExchangeStrategies.builder()
                        .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(1024 * 5000))
                        .build())
                .baseUrl(baseUrl.toString())
                .build();


        TravelTimeService service = new TravelTimeService(repo, new HttpAdapter(webClient), serviceConfig);
        controller = new HttpSseController(service);
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


    void expectFullCollectionOnFirstRequest() throws InterruptedException {
        Hooks.onOperatorDebug();

        json1 = getString("fc1.geojson");
        json2 = getString("fc2.geojson");
        json3 = getString("fc3.geojson");

        server.enqueue(
                new MockResponse()
                        .setResponseCode(200)
                        .setBody(json1));

        WebTestClient client = WebTestClient.bindToController(controller).build();

        ParameterizedTypeReference<ServerSentEvent<Feature>> typeRef = new ParameterizedTypeReference<>() {};

        Flux<ServerSentEvent<Feature>> featureEvent = client.get()
                .uri("/featureSubscription")
                .accept(MediaType.TEXT_EVENT_STREAM)
                .exchange()
                .expectStatus().isOk()
                .returnResult(typeRef)
                .getResponseBody();

        StepVerifier.create(featureEvent) //
                .thenAwait(Duration.ofSeconds(3))
                .thenCancel()
                .verify();

        Thread.sleep(5);

        var y =  databaseClient.execute("SELECT * FROM travel_time_entity;") //
                .fetch()
                .all()
                .count()
                .block();

        System.out.println(y);
    }

    static void tearDown() throws IOException {
        server.shutdown();
    }
}
