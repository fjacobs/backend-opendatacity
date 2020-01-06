package com.dynacore.livemap.core.tools;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.geojson.FeatureCollection;
import org.springframework.util.Assert;
import org.springframework.util.ResourceUtils;

import java.io.BufferedReader;
import java.io.File;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


/*
      Utility class to convert files with GeoJson contents to objects.
      Used as a helper to import GeoJson files into the database or for dev/testing purposes.
*/
public class FileToGeojson {

     /*
      *   Converts all files in a folder.
      *   @param folderName  folderName must be relative to the resources folder.
     */
    public static List<FeatureCollection> readCollection(String folderName) {

        File folder = new File(FileToGeojson.class.getResource(folderName).getPath());

        return Arrays.stream(Objects.requireNonNull(folder.listFiles()))
                .filter(File::isFile)
                .map(file -> {
                    FeatureCollection fc = null;
                    try {
                        fc = new ObjectMapper().readValue(getString(folderName.concat(file.getName())), FeatureCollection.class);
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                    }
                    return fc;
                })
                .collect(Collectors.toList());

    }

    private static String getString(String fileName) {

        String featureCollection = "";
        try {
            Path path = ResourceUtils.getFile(FileToGeojson.class.getResource(fileName)).toPath();
            Charset charset = StandardCharsets.UTF_8;
            try (BufferedReader reader = Files.newBufferedReader(path, charset)) {
                String tempLine;
                while ((tempLine = reader.readLine()) != null) {
                    featureCollection = "" + tempLine;
                }
            }
        } catch (Exception error) {
            error.printStackTrace();
            Assert.state(false, "Error reading test geojson file '" + fileName + "'  in resources: " + error.toString());
        }
        return featureCollection;
    }
}
