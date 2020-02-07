package com.dynacore.livemap.core.service;

import com.dynacore.livemap.core.model.TrafficFeature;

import java.time.OffsetDateTime;
import java.util.function.Function;

import static com.dynacore.livemap.core.model.TrafficFeature.OUR_RETRIEVAL;

public class DistinctUtil {

  public static Function<TrafficFeature, Integer> hashCodeNoRetDate =
      (TrafficFeature trafficFeature) -> {
        int result;
        if (trafficFeature.getProperties().containsKey(OUR_RETRIEVAL)) {
            OffsetDateTime ourTimestamp;

            try{

            if(trafficFeature.getProperty(OUR_RETRIEVAL) instanceof String) {
                ourTimestamp = OffsetDateTime.parse(trafficFeature.getProperty(OUR_RETRIEVAL));
            } else {
                ourTimestamp = trafficFeature.getProperty(OUR_RETRIEVAL);
            }
            }catch(Exception error) {
                throw new RuntimeException("Our timestamp could not be converted to OffsetDateTime");
            }

          trafficFeature.getProperties().remove(OUR_RETRIEVAL);
          result = 31 * (trafficFeature.getProperties() != null ? trafficFeature.getProperties().hashCode() : 0);
          result = 31 * result + (trafficFeature.getGeometry() != null ? trafficFeature.getGeometry().hashCode() : 0);
          result = 31 * result + (trafficFeature.getId() != null ? trafficFeature.getId().hashCode() : 0);
          trafficFeature.setProperty(OUR_RETRIEVAL, ourTimestamp);
        } else {
          result = trafficFeature.hashCode();
        }
        return result;
      };
}
