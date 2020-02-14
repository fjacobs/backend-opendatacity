package com.dynacore.livemap.core.service;

import com.dynacore.livemap.core.model.TrafficFeature;

import java.time.OffsetDateTime;
import java.util.function.Function;

public class DistinctUtil {

  public static Function<TrafficFeature, Integer> hashCodeNoRetDate =
      (TrafficFeature trafficFeature) -> {
        int result = 0;

        try {
          if (trafficFeature.getOurRetrieval() != null) {
            OffsetDateTime ourTimestamp = trafficFeature.getOurRetrieval();
            trafficFeature.setOurRetrieval(OffsetDateTime.MAX);
            OffsetDateTime theirTimestamp = trafficFeature.getPubDate();
            trafficFeature.setPubDate(OffsetDateTime.MAX);
            result = trafficFeature.hashCode();
//            System.out.println(trafficFeature.getId() + ": " + result);
//            System.out.println(trafficFeature);

            trafficFeature.setOurRetrieval(ourTimestamp);
            trafficFeature.setPubDate(theirTimestamp);

          } else {
            result = trafficFeature.hashCode();
          }

        } catch (Exception error) {
           error.printStackTrace();
        }
        return result;
      };
}
