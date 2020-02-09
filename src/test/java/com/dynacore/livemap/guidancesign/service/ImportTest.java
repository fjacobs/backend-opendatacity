package com.dynacore.livemap.guidancesign.service;

import com.dynacore.livemap.core.adapter.GeoJsonAdapter;
import com.dynacore.livemap.core.service.FeatureImporter;
import com.dynacore.livemap.guidancesign.domain.GuidanceSignFeature;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.mockwebserver.MockWebServer;
import org.geojson.Feature;
import org.geojson.FeatureCollection;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Hooks;

import java.time.Duration;
import java.time.OffsetDateTime;

import static com.dynacore.livemap.core.model.TrafficFeatureInterface.OUR_RETRIEVAL;

public class ImportTest {

  static String jsonCorrectRoad =
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

  String jsonGuidOriginal =
      "{ \"type\":\"FeatureCollection\", \"features\":[ { \"type\":\"Feature\", \"Id\":\"34280b21-5a1c-429d-94c0-a1706b4e3f62\","
          + " \"geometry\":{ \"type\":\"Point\", \"coordinates\":[ 4.944938, 52.315838 ] }, \"properties\":{ \"Name\":\"FJ462B13 - ZO-B13 Burg.Stramanweg 02510/080\", \"PubDate\":\"2020-02-08T21:25:41.900Z\", "
          + "\"Removed\":\"false\", \"Type\":\"guidancesign\", \"State\":\"ok\", \"ParkingguidanceDisplay\":[ { \"Id\":\"397a43d2-355a-42fe-ab5a-1f9f0ffb4624\", \"OutputDescription\":\"VRIJ\", "
          + "\"Description\":\"ZO-B13_VVX_02510/080 P+R Arena\", \"Type\":\"VVX\", \"Output\":\"VRIJ\" } ] } }, { \"type\":\"Feature\", \"Id\":\"768730c0-de14-40f3-a59a-2ef423a68e7f\","
          + " \"geometry\":{ \"type\":\"Point\", \"coordinates\":[ 4.911008, 52.360648 ] }, \"properties\":{ \"Name\":\"FJ212CE_VVXN_63225/003_Mauritskade\", \"PubDate\":\"2020-02-08T21:25:41.900Z\", "
          + "\"Removed\":\"false\", \"Type\":\"guidancesign\", \"State\":\"ok\", \"ParkingguidanceDisplay\":[ { \"Id\":\"e3c2295d-bd21-462a-b1f7-c9bbe7730f84\", \"OutputDescription\":\"\", \"Description\":\"FJ212CE_VVXN558_63225/003_Museumkwartier\","
          + " \"Type\":\"VVXNUMERIC\", \"Output\":\"700\" }, { \"Id\":\"fe1c9e78-6ee3-4415-8e45-fe600ad44adc\", \"OutputDescription\":\"\", \"Description\":\"FJ212CE_VVXN559_63225/003_Muntplein\", \"Type\":\"VVXNUMERIC\", \"Output\":\"50\" }, "
          + "{ \"Id\":\"a7b4a301-c672-4ed1-8b99-a07a2815f65c\", \"OutputDescription\":\"\", \"Description\":\"FJ212CE_VVXN560_63225/003_Burgwallen\", \"Type\":\"VVXNUMERIC\", \"Output\":\"550\" } ] } } ] }";

  String jsonGuid2 =
      "{ \"type\":\"FeatureCollection\", \"features\":[ { \"type\":\"Feature\", \"Id\":\"34280b21-5a1c-429d-94c0-a1706b4e3f62\","
          + " \"geometry\":{ \"type\":\"Point\", \"coordinates\":[ 4.944938, 52.315838 ] }, \"properties\":{ \"Name\":\"FJ462B13 - ZO-B13 Burg.Stramanweg 02510/080\", \"PubDate\":\"2020-02-08T21:25:41.900Z\", "
          + "\"Removed\":\"false\", \"Type\":\"guidancesign\", \"State\":\"ok\", \"ParkingguidanceDisplay\":[ {  \"OutputDescription\":\"VRIJ\", "
          + "\"Description\":\"ZO-B13_VVX_02510/080 P+R Arena\", \"Type\":\"VVX\", \"Output\":\"VRIJ\" } ] } }, { \"type\":\"Feature\", \"Id\":\"768730c0-de14-40f3-a59a-2ef423a68e7f\","
          + " \"geometry\":{ \"type\":\"Point\", \"coordinates\":[ 4.911008, 52.360648 ] }, \"properties\":{ \"Name\":\"FJ212CE_VVXN_63225/003_Mauritskade\", \"PubDate\":\"2020-02-08T21:25:41.900Z\", "
          + "\"Removed\":\"false\", \"Type\":\"guidancesign\", \"State\":\"ok\", \"ParkingguidanceDisplay\":[ { \"Id\":\"e3c2295d-bd21-462a-b1f7-c9bbe7730f84\", \"OutputDescription\":\"\", \"Description\":\"FJ212CE_VVXN558_63225/003_Museumkwartier\","
          + " \"Type\":\"VVXNUMERIC\", \"Output\":\"700\" }, { \"Id\":\"fe1c9e78-6ee3-4415-8e45-fe600ad44adc\", \"OutputDescription\":\"\", \"Description\":\"FJ212CE_VVXN559_63225/003_Muntplein\", \"Type\":\"VVXNUMERIC\", \"Output\":\"50\" }, "
          + "{ \"Id\":\"a7b4a301-c672-4ed1-8b99-a07a2815f65c\", \"OutputDescription\":\"\", \"Description\":\"FJ212CE_VVXN560_63225/003_Burgwallen\", \"Type\":\"VVXNUMERIC\", \"Output\":\"550\" } ] } } ] }";
  static MockWebServer server;
  static GuidanceSignService service;
  static GuidanceSignServiceConfig serviceConfig;

  @Test
  public void deserializeTest() throws JsonProcessingException {
    FeatureCollection featureCollection =
        new ObjectMapper()
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            .readValue(jsonGuidOriginal, FeatureCollection.class);
  }

  @Test
  void featureImporterTest() throws JsonProcessingException {
    Hooks.onOperatorDebug();

    GeoJsonAdapter smallFcAdapter =
        (serviceConfig) ->
            Flux.just(
                new ObjectMapper()
                    .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                    .readValue(jsonGuidOriginal, FeatureCollection.class));

    FeatureImporter<GuidanceSignFeature> importer =
            feature -> {
              feature.setProperty(OUR_RETRIEVAL, OffsetDateTime.now().toString());

              GuidanceSignFeature guidFeature = new GuidanceSignFeature(feature);

              return guidFeature;
            };

    smallFcAdapter
        .adapterHotSourceReq(Duration.ofSeconds(1))
        .doOnNext(System.out::println)
        .flatMapIterable(FeatureCollection::getFeatures)
        .map(importer::importFeature)
        .doOnNext(System.out::println)
        .blockLast();
  }
}
