package com.dynacore.livemap.configuration.adapter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.geojson.FeatureCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/*
   Utility class to convert files with GeoJson contents to objects.
   Used as a helper to import GeoJson files into the database or for dev/testing purposes.
*/
public class FileToGeojson  {

  private static final Logger log = LoggerFactory.getLogger(FileToGeojson.class);


  private static Set<String> listFilesUsingJavaIO(String dir) {
    return Stream.of(Objects.requireNonNull(new File(dir).listFiles()))
            .filter(file -> !file.isDirectory())
            .map(File::getName)
            .collect(Collectors.toSet());
  }
  /*
   *   Converts all files in a folder.
   *   @param folderName  folderName must be relative to the resources folder.
   */
  public static List<FeatureCollection> readCollection(File folder) {

    log.info("Importing geojson folder: " + folder.getName());

    File[] files = folder.listFiles();

    Arrays.sort(Objects.requireNonNull(files));
    return Arrays.stream(files)
        .filter(File::isFile)
        .map(
            file -> {
              FeatureCollection fc = null;
              try {
                fc =
                    new ObjectMapper()
                    .configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true)
                    .readValue(
                            file,FeatureCollection.class);
              } catch (JsonProcessingException e) {
                log.error("Error reading: " + file.getName());
                throw new RuntimeException(e);
              } catch (IOException e) {
                e.printStackTrace();
                assert false;
                throw new RuntimeException(e);

              }
              return fc;
            })
        .collect(Collectors.toList());
  }
}
//guidancesign2020-02-09_00-35-53.GuidanceSign.json
