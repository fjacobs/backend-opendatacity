/*
 * Copyright 2002-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.dynacore.livemap.traveltime.service;

import com.dynacore.livemap.core.geojson.TrafficFeature;
import com.dynacore.livemap.core.service.GeoJsonAdapter;
import com.dynacore.livemap.testing.subscriber.AssertSubscriber;
import com.dynacore.livemap.traveltime.domain.TravelTimeFeature;
import com.dynacore.livemap.traveltime.repo.TravelTimeEntity;
import com.dynacore.livemap.traveltime.repo.TravelTimeRepo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.mockwebserver.MockWebServer;
import org.geojson.Feature;
import org.geojson.FeatureCollection;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Hooks;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ActiveProfiles("dev")
class TravelTimeServiceTest {

  static String jsonCorrect =
      "{\"type\":\"FeatureCollection\",\"crs\":{\"type\":\"name\",\"properties\":{\"name\":\"urn:ogc:def:crs:OGC:1.3:CRS84\"}},"
          + "\"features\":[{\"type\":\"Feature\",\"properties\":{\"Id\":\"RWS01_MONIBAS_0091hrl0356ra0\",\""
          + "Name\":\"0091hrl0356ra0\",\"Type\":\"H\",\"Timestamp\":\"2019-10-16T15:52:00Z\",\"Length\":623,\""
          + "Velocity\":0},\"geometry\":{\"type\":\"LineString\",\"coordinates\":[[4.776645712715283,52.338380232953895]"
          + ",[4.776853788479121,52.33827956952142],[4.77842037340242,52.337460757548655],[4.778671519814815,52.33733621949967]"
          + ",[4.780652279562285,52.336267847567214],[4.782159793662865,52.33546665913931],[4.782751047173977,52.335146118375064],[4.78306134179851,52.33498592177948],[4.78356224185475,52.33472011500613]]}}"
          + ",{\"type\":\"Feature\",\"properties\":{\"Id\":\"RWS01_MONIBAS_0051hrl0092rb0\",\"Name\":\"0051hrl0092rb0\",\"Type\":\"H\",\"Timestamp\":\"2019-10-16T15:52:00Z\",\"Length\":627,\"Traveltime\":22,\""
          + "Velocity\":0},\"geometry\":{\"type\":\"LineString\",\"coordinates\":[[4.747882941552497,52.36389974515355],"
          + "[4.747441699702729,52.36372959463498],[4.747144894615104,52.36352614166333],[4.74667824897942,52.36325203253315],[4.745704027145298,52.36263386826604],"
          + "[4.744950288694216,52.36210864000742],[4.744384634980363,52.361635811770356],[4.743800919091215,52.36117021663649],[4.743137406016038,52.36062712270663],"
          + "[4.74267909704837,52.36019523441274],[4.742046603115947,52.359578948936196]]}},{\"type\":\"Feature\",\"properties\":{\"Id\":\"RWS01_MONIBAS_0091hrl0059ra1\","
          + "\"Name\":\"0091hrl0059ra1\",\"Type\":\"H\",\"Timestamp\":\"2019-10-16T15:52:00Z\",\"Length\":458,\"Velocity\":0},\"geometry\":{\"type\":\"LineString\",\"coordinates\""
          + ":[[5.004879014653774,52.324294987008926],[5.005005993142421,52.324559388367234],[5.005049301921006,52.32465911236145],[5.005433357558088,52.325807142107685],"
          + "[5.005562368354391,52.32628371039965],[5.005723592066905,52.32730043716067],[5.005711772825435,52.327993424971154],[5.005687110573994,52.32836111625229]]}}]}";

  static MockWebServer server;
  static TravelTimeReactorService service;
  static TravelTimeServiceConfig serviceConfig;

  @Test
  void expectThreeFeatures() throws JsonProcessingException {
    Hooks.onOperatorDebug();

    TravelTimeRepo repo = mock(TravelTimeRepo.class);

    when(repo.isNew(any(TravelTimeEntity.class))).thenReturn(Mono.just(false));

    serviceConfig = new TravelTimeServiceConfig();
    serviceConfig.setInitialDelay(Duration.ZERO);
    serviceConfig.setRequestInterval(Duration.ofSeconds(1));
    serviceConfig.setSaveToDbEnabled(false);

    GeoJsonAdapter smallFc =
        (serviceConfig) ->
            Flux.just(new ObjectMapper().readValue(jsonCorrect, FeatureCollection.class));


    service = new TravelTimeReactorService(repo, smallFc, serviceConfig);
    service
        .getLiveData()
        .as(StepVerifier::create)
        .consumeNextWith(ssss-> System.out.println(ssss.getProperties().get("Id") ))
    //    .expectNextMatches(ft -> ft.getId().matches("RWS01_MONIBAS_0091hrl0356ra0"))
        .expectNextCount(2)
        .verifyComplete();
  }

  @Test
  void distinctKeySelectorTest() {

    TrafficFeature feature1 = new TrafficFeature();
    feature1.setId("Feature1");
    feature1.setProperty("prop1", "value1");
    feature1.setProperty("prop2", 2);

    TrafficFeature feature2 = new TrafficFeature();
    feature2.setId("Feature1");
    feature2.setProperty("prop1", "value1");
    feature2.setProperty("prop2", 2);

    TrafficFeature feature3 = new TrafficFeature();
    feature3.setId("Feature1");
    feature3.setProperty("prop1", "value1");
    feature3.setProperty("prop2", 5);

    TrafficFeature feature4 = new TrafficFeature();
    feature4.setId("Feature1");
    feature4.setProperty("prop1", "value1");
    feature4.setProperty("prop2", 6);

    FeatureCollection fc = new FeatureCollection();
    fc.setFeatures(Arrays.asList(feature1, feature2, feature3, feature4));

    AssertSubscriber<Feature> ts = AssertSubscriber.create();

    Flux.fromIterable(fc.getFeatures()).cast(TrafficFeature.class).distinct(DistinctUtil.hashCodeNoRetDate).subscribe(ts);

    ts.assertValues(feature1, feature3, feature4).assertComplete().assertNoError();
  }

  @Test
  void emitDistinctFeatures() throws JsonProcessingException {
    Hooks.onOperatorDebug();

    final TrafficFeature feature1_1 = new TravelTimeFeature();
    final TrafficFeature feature2_1 = new TravelTimeFeature();
    final TrafficFeature feature1_2 = new TravelTimeFeature();
    final TrafficFeature feature2_2 = new TravelTimeFeature();
    final TrafficFeature feature1_3 = new TravelTimeFeature();
    final TrafficFeature feature2_3 = new TravelTimeFeature();

    GeoJsonAdapter prop3Changed =
        (interval) -> {

          // Emit two new features:
          feature1_1.setId("Feature1");
          feature1_1.setProperty("prop1", "value1");
          feature1_1.setProperty("prop2", 2);
          feature1_1.setProperty("retrievedFromThirdParty", "2019-10-16T15:52:00Z");

          feature2_1.setId("Feature2");
          feature2_1.setProperty("prop1", "value1");
          feature2_1.setProperty("prop2", 2);
          feature2_1.setProperty("retrievedFromThirdParty", "2019-10-16T15:52:00Z");

          FeatureCollection fc = new FeatureCollection();
          fc.setFeatures(Arrays.asList(feature1_1, feature2_1));

          // Emit one feature:
          // -F1 prop1 changed
          FeatureCollection fc2 = new FeatureCollection();
          feature1_2.setId("Feature1");
          feature1_2.setProperty("prop1", "value2");
          feature1_2.setProperty("prop2", 2);
          feature1_2.setProperty("retrievedFromThirdParty", "2019-10-16T15:53:00Z");

          feature2_2.setId("Feature2");
          feature2_2.setProperty("prop1", "value1");
          feature2_2.setProperty("prop2", 2);
          feature2_2.setProperty("retrievedFromThirdParty", "2019-10-16T15:53:00Z");

          fc2.setFeatures(Arrays.asList(feature1_2, feature2_2));

          // Emit two features:
          // - F1 Changed back to first it's first form (ie. Assert that literal 'distinct until
          // changed'
          //      was performed instead of Reactor's version of distinct(), or distinctUntilChanged)
          // - F2 Changed (first time)
          FeatureCollection fc3 = new FeatureCollection();
          feature1_3.setId("Feature1");
          feature1_3.setProperty("prop1", "value1");
          feature1_3.setProperty("prop2", 2);
          feature1_3.setProperty("retrievedFromThirdParty", "2019-10-16T15:54:00Z");

          feature2_3.setId("Feature2");
          feature2_3.setProperty("prop1", "value1");
          feature2_3.setProperty("prop2", 2);
          feature2_3.setProperty("retrievedFromThirdParty", "2019-10-16T15:54:00Z");
          fc3.setFeatures(Arrays.asList(feature1_3, feature2_3));

          return Flux.just(fc, fc2, fc3);
        };

    TravelTimeRepo repo = mock(TravelTimeRepo.class);
    TravelTimeServiceConfig serviceConfig = new TravelTimeServiceConfig();
    serviceConfig.setElementDelay(Duration.ofSeconds(0));
    serviceConfig.setInitialDelay(Duration.ZERO);
    serviceConfig.setRequestInterval(Duration.ofSeconds(0));
    serviceConfig.setSaveToDbEnabled(false);

    TravelTimeReactorService service =
        new TravelTimeReactorService(repo, prop3Changed, serviceConfig);

    StepVerifier.create(service.getLiveData())
        .expectNext(feature1_1)
        .expectNext(feature2_1)
        .expectNext(feature1_2)
        .expectNext(feature1_3)
        .verifyComplete();
  }

  @Test
  void getFeatureCollection() throws JsonProcessingException {
    Hooks.onOperatorDebug();
    TravelTimeRepo repo = mock(TravelTimeRepo.class);

    when(repo.isNew(any(TravelTimeEntity.class))).thenReturn(Mono.just(false));

    serviceConfig = new TravelTimeServiceConfig();
    serviceConfig.setInitialDelay(Duration.ZERO);
    serviceConfig.setRequestInterval(Duration.ofSeconds(0));
    serviceConfig.setSaveToDbEnabled(false);

    FeatureCollection fc = new ObjectMapper().readValue(jsonCorrect, FeatureCollection.class);

    GeoJsonAdapter smallFc = (serviceConfig) -> Flux.just(fc);

    smallFc.requestHotSourceFc(Duration.ofSeconds(1)).blockLast();

    //    service = new TravelTimeReactorService(repo, smallFc, serviceConfig);
    //    service
    //        .getFeatureCollection()
    //        .as(StepVerifier::create)
    //        .expectNextMatches(ft -> ft.getFeatures().size() == 3)
    //        .verifyComplete();
  }
}
