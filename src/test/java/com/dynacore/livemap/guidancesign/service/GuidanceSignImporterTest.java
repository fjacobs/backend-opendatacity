package com.dynacore.livemap.guidancesign.service;

import com.dynacore.livemap.core.adapter.GeoJsonAdapter;
import com.dynacore.livemap.guidancesign.domain.InnerDisplayModel;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.geojson.FeatureCollection;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Hooks;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.UUID;

import static junit.framework.TestCase.assertTrue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

class GuidanceSignImporterTest {

  @Test
  void importFeature() throws JsonProcessingException {

    String jsonGuidOriginal =
        "{ \"type\":\"FeatureCollection\", \"features\":[ { \"type\":\"Feature\", \"Id\":\"34280b21-5a1c-429d-94c0-a1706b4e3f62\","
            + " \"geometry\":{ \"type\":\"Point\", \"coordinates\":[ 4.944938, 52.315838 ] }, \"properties\":{ \"Name\":\"FJ462B13 - ZO-B13 Burg.Stramanweg 02510/080\", \"PubDate\":\"2020-02-08T21:25:41.900Z\", "
            + "\"Removed\":\"false\", \"Type\":\"guidancesign\", \"State\":\"ok\", \"ParkingguidanceDisplay\":[ { \"Id\":\"397a43d2-355a-42fe-ab5a-1f9f0ffb4624\", \"OutputDescription\":\"VRIJ\", "
            + "\"Description\":\"ZO-B13_VVX_02510/080 P+R Arena\", \"Type\":\"VVX\", \"Output\":\"VRIJ\" } ] } }, { \"type\":\"Feature\", \"Id\":\"768730c0-de14-40f3-a59a-2ef423a68e7f\","
            + " \"geometry\":{ \"type\":\"Point\", \"coordinates\":[ 4.911008, 52.360648 ] }, \"properties\":{ \"Name\":\"FJ212CE_VVXN_63225/003_Mauritskade\", \"PubDate\":\"2020-02-08T21:25:41.900Z\", "
            + "\"Removed\":\"false\", \"Type\":\"guidancesign\", \"State\":\"ok\", \"ParkingguidanceDisplay\":[ { \"Id\":\"e3c2295d-bd21-462a-b1f7-c9bbe7730f84\", \"OutputDescription\":\"\", \"Description\":\"FJ212CE_VVXN558_63225/003_Museumkwartier\","
            + " \"Type\":\"VVXNUMERIC\", \"Output\":\"700\" }, { \"Id\":\"fe1c9e78-6ee3-4415-8e45-fe600ad44adc\", \"OutputDescription\":\"\", \"Description\":\"FJ212CE_VVXN559_63225/003_Muntplein\", \"Type\":\"VVXNUMERIC\", \"Output\":\"50\" }, "
            + "{ \"Id\":\"a7b4a301-c672-4ed1-8b99-a07a2815f65c\", \"OutputDescription\":\"\", \"Description\":\"FJ212CE_VVXN560_63225/003_Burgwallen\", \"Type\":\"VVXNUMERIC\", \"Output\":\"550\" } ] } } ] }";
    GeoJsonAdapter smallFcAdapter =
        (serviceConfig) ->
            Flux.just(
                new ObjectMapper()
                    .configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true)
                    .readValue(jsonGuidOriginal, FeatureCollection.class));
    Hooks.onOperatorDebug();
    GuidanceSignImporter guidanceSignImporter = new GuidanceSignImporter();
    smallFcAdapter
        .adapterHotSourceReq(Duration.ofMillis(10))
        .doOnNext(System.out::println)
        .flatMapIterable(FeatureCollection::getFeatures)
        .map(guidanceSignImporter::importFeature)
        .as(StepVerifier::create)
        .expectSubscription()
        .consumeNextWith(
            guidanceSignFeature -> {
                assertThat(guidanceSignFeature.getId(), is("34280b21-5a1c-429d-94c0-a1706b4e3f62"));
                assertThat(guidanceSignFeature.getType(), is("guidancesign"));
                assertThat(guidanceSignFeature.getRemoved(), is(false));
                assertThat(guidanceSignFeature.getState(), is("ok"));
               assertTrue(guidanceSignFeature.getPubDate().isEqual(OffsetDateTime.parse("2020-02-08T21:25:41.900Z")));
                InnerDisplayModel inner = guidanceSignFeature.getChildDisplays().stream().findFirst().orElseThrow();
                assertThat(inner.getId(), is(UUID.fromString("397a43d2-355a-42fe-ab5a-1f9f0ffb4624")));
                assertThat(inner.getDescription(), is("ZO-B13_VVX_02510/080 P+R Arena"));
                assertThat(inner.getOutput(), is("VRIJ"));
                assertThat(inner.getOutputDescription(), is("VRIJ"));
                assertThat(inner.getType(), is("VVX"));
            })
        .consumeNextWith(
            guidanceSignFeature -> {
              assertThat(guidanceSignFeature.getChildDisplays().size(), is(3));
            })
        .thenCancel()
        .verify();
    Hooks.onOperatorDebug();
  }
}
