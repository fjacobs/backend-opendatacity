package com.dynacore.livemap.traveltime;

import com.dynacore.livemap.common.http.HttpClientFactory;
import com.dynacore.livemap.traveltime.repo.TravelTimeRepo;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.NoArgsConstructor;
import org.geojson.Feature;
import org.geojson.FeatureCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.util.Assert;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.ConnectableFlux;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@NoArgsConstructor
@Primary
public class FrontendTester implements TravelTimeService {

    private ConnectableFlux<Feature> sharedFlux;
    private WebClient webClient;

    @Autowired
    public FrontendTester(TravelTimeRepo repo, HttpClientFactory httpClientFactory) {
        ReactorClientHttpConnector httpConnector = new ReactorClientHttpConnector(httpClientFactory.autoConfigHttpClient(SOURCEURL));
        webClient = WebClient.builder().clientConnector(httpConnector)
                .build();
    }


    public Mono<FeatureCollection> fileFeatureCollection() {

        return Flux.just(1)
                .flatMapIterable(x ->
                         loadFromFile().getFeatures()
                )
                //.take(10)
                .map(this::processFeature)
        //        .map(this::convertToEntity)
//                .filterWhen(this::isPubdateUnique)
           //     .filterWhen(repo::didPropertiesChange)
           //     .doOnNext(x-> logger.info("Changed Features: " + x.getId()))

                //saveToDB
                .collectList()

                .map(features -> {
                    FeatureCollection collection = new FeatureCollection();
                     collection.setFeatures(features);
                    return collection;
                });
    }


//    var speedColors = {
//            0: "#D0D0D0",
//            1: "#BE0000",
//            30: "#FF0000",
//            50: "#FF9E00",
//            70: "#FFFF00",
//            90: "#AAFF00",
//            120: "#00B22D"


    int timer = 0;
    public FeatureCollection loadFromFile() {
//      Mono<FeatureCollection> test = Mono.empty();
        timer++;
        if ((timer == 0) || (timer > 3)) {
            timer = 1;
        }

        FeatureCollection featureColl = null;
        try {
            String file = "fc" + timer + ".json";
            byte[] content = Files.readAllBytes(Paths.get(file));
            logger.error("Loaded file " + file);

            ObjectMapper objectMapper = new ObjectMapper();
            featureColl = objectMapper.readValue(content, FeatureCollection.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Assert.notNull(featureColl, "Error: did not load file: ");
        return featureColl;
    }








}
