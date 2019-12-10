package com.dynacore.livemap.traveltime;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import io.rsocket.transport.netty.client.WebsocketClientTransport;
import org.geojson.Feature;
import org.junit.Assert;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.r2dbc.core.DatabaseClient;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.util.ResourceUtils;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

import com.dynacore.livemap.configuration.PostgresConfig;
import com.dynacore.livemap.configuration.HttpClientFactoryConfig;
import com.dynacore.livemap.testing.database.H2TestSupport;
import com.dynacore.livemap.traveltime.controller.HttpController;
import com.dynacore.livemap.traveltime.repo.TravelTimeRepo;
import com.dynacore.livemap.traveltime.service.ServiceConfig;
import com.dynacore.livemap.traveltime.service.TravelTimeService;

import okhttp3.HttpUrl;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import reactor.core.publisher.Hooks;


@SpringBootTest( properties = "spring.main.web-application-type=reactive",
                 webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class RSocketIntegrationTest {

    @Value("${spring.rsocket.server.port}")
    private int serverPort;

    @Autowired
    private RSocketRequester.Builder builder;

    static String json1;
    static String json2;
    static String json3;
    static String notAjson = "this is not a json";

    static HttpController controller;
    static MockWebServer openDataServer;
    static DatabaseClient databaseClient;

    @BeforeAll
    static void setUp() {

        openDataServer = new MockWebServer();

        HttpUrl baseUrl = openDataServer.url("/v1/chat/");

        ServiceConfig serviceConfig = new ServiceConfig();
        serviceConfig.setInitialDelay(0);
        serviceConfig.setRequestInterval(1);
        serviceConfig.setUrl(baseUrl.url().toString());

        databaseClient = DatabaseClient.create(new PostgresConfig().connectionFactory());

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


        TravelTimeService service = new TravelTimeService(repo, webClient, serviceConfig);
        controller = new HttpController(service);
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
    void expectFullCollectionOnFirstRequest() throws InterruptedException {
        Hooks.onOperatorDebug();

        json1 = getString("fc1.geojson");
        json2 = getString("fc2.geojson");
        json3 = getString("fc3.geojson");

        openDataServer.enqueue(
                new MockResponse()
                        .setResponseCode(200)
                        .setBody(json1));

        openDataServer.enqueue(
                new MockResponse()
                        .setResponseCode(200)
                        .setBody(json2));

        openDataServer.enqueue(
                new MockResponse()
                        .setResponseCode(200)
                        .setBody(json3));

        RSocketRequester requester =  builder.connect( WebsocketClientTransport
                                             .create(serverPort))
                                             .block();

        requester.route("traveltime-message")
                .retrieveFlux(Feature.class)
                .doOnNext(Feature::getId)
                .blockLast();
    }


    @AfterAll
    static void tearDown() throws IOException {
        openDataServer.shutdown();
    }
}
