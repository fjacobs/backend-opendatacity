package com.dynacore.livemap.traveltime.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.geojson.Feature;
import org.geojson.FeatureCollection;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.ResourceUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;

@Profile("dev")
@Component
public class FileRetriever implements OpenDataRetriever {

    public Flux<FeatureCollection> requestFeatures() {

        String json1 = getString("fc1.geojson");
        String json2 = getString("fc2.geojson");
        String json3 = getString("fc3.geojson");

        return Flux.just(json1, json2, json3)
                        .delayElements(Duration.ofSeconds(15))
                        .map(jsonString -> {
                            FeatureCollection fc1 = null;
                            try {
                                fc1 = new ObjectMapper().readValue(jsonString, FeatureCollection.class);
                            } catch (JsonProcessingException e) {
                                e.printStackTrace();
                            }
                            return fc1;
                        })
                        .cast(FeatureCollection.class);
    }

    private String getString(String fileName) {
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
                } catch (IOException x) {
                    throw x;
                }
            } catch (Exception e) {
                throw e;
            }
        } catch (Exception error) {
            error.printStackTrace();
            Assert.state(false, "Error reading test geojson file '" + fileName + "'  in test/resources: " + error.toString());
        }
        return featureCollection;
    }

}
