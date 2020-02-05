package com.dynacore.livemap.traveltime.service;

import com.dynacore.livemap.core.geojson.TrafficFeature;

import java.util.function.Function;

import static com.dynacore.livemap.core.geojson.TrafficFeature.OUR_RETRIEVAL;

public class DistinctUtil {

  public static Function<TrafficFeature, Integer> hashCodeNoRetDate =
      (TrafficFeature road) -> {
        int result;
        if (road.getProperties().containsKey(OUR_RETRIEVAL)) {
          String retDate = road.getProperty(OUR_RETRIEVAL);
          road.getProperties().remove(OUR_RETRIEVAL);
          result = 31 * (road.getProperties() != null ? road.getProperties().hashCode() : 0);
          result = 31 * result + (road.getGeometry() != null ? road.getGeometry().hashCode() : 0);
          result = 31 * result + (road.getId() != null ? road.getId().hashCode() : 0);
          road.setProperty(OUR_RETRIEVAL, retDate);
        } else {
          result = road.hashCode();
        }
        return result;
      };
}
