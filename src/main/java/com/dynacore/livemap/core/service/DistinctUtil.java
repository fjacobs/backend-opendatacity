package com.dynacore.livemap.core.service;

import com.dynacore.livemap.core.model.TrafficFeature;

import java.util.function.Function;

import static com.dynacore.livemap.core.model.TrafficFeature.OUR_RETRIEVAL;

public class DistinctUtil {

  public static Function<TrafficFeature, Integer> hashCodeNoRetDate =
      (TrafficFeature trafficFeature) -> {
        int result;
        if (trafficFeature.getProperties().containsKey(OUR_RETRIEVAL)) {
          String retDate = trafficFeature.getProperty(OUR_RETRIEVAL);
          trafficFeature.getProperties().remove(OUR_RETRIEVAL);
          result = 31 * (trafficFeature.getProperties() != null ? trafficFeature.getProperties().hashCode() : 0);
          result = 31 * result + (trafficFeature.getGeometry() != null ? trafficFeature.getGeometry().hashCode() : 0);
          result = 31 * result + (trafficFeature.getId() != null ? trafficFeature.getId().hashCode() : 0);
          trafficFeature.setProperty(OUR_RETRIEVAL, retDate);
        } else {
          result = trafficFeature.hashCode();
        }
        return result;
      };
}
