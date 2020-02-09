package com.dynacore.livemap.guidancesign.domain;

import com.dynacore.livemap.core.model.TrafficFeature;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.ToString;
import org.geojson.Feature;

import java.time.OffsetDateTime;
import java.util.*;

import static java.util.stream.Collectors.toList;

@ToString
public class GuidanceSignFeature extends TrafficFeature {

  private PropertiesImpl customProperties;

  private static final String PUB_DATE = "PubDate"; // Different suppliers use different pubdates
  private static final String TYPE = "Type";
  private static final String STATE = "State";
  private static final String REMOVED = "Removed";

  // Inner displays:
  private static final String INNER_DISPLAY = "ParkingguidanceDisplay";
  private static final String DESCRIPTION = "Description";
  private static final String OUTPUT = "Output";
  private static final String OUTPUT_DESCRIPTION = "OutputDescription";

  public GuidanceSignFeature(Feature feature) {
    super(feature, PUB_DATE);

    unpackNested(feature.getProperties());
  }

  @JsonIgnore
  public boolean getRemoved() {
    return customProperties.removed;
  }

  @JsonIgnore
  public String getState() {
    return customProperties.state;
  }

  @JsonIgnore
  public String getType() {
    return customProperties.type;
  }

  public List<InnerDisplayModel> getChildDisplays() {
    return customProperties.getInnerDisplayModelList();
  }

  @JsonProperty("properties")
  public void unpackNested(Map<String, Object> prop) throws IllegalStateException {
    List<InnerDisplayModel> childDisplays =
        ((ArrayList<LinkedHashMap<String, String>>) prop.get(INNER_DISPLAY))
            .stream()
                .map(
                    dispMap ->
                        new InnerDisplayModel.Builder()
                            .id(UUID.fromString(dispMap.get(ID)))
                            .description(dispMap.get(DESCRIPTION))
                            .output(dispMap.get(OUTPUT))
                            .outputDescription(dispMap.get(OUTPUT_DESCRIPTION))
                            .type(dispMap.get(TYPE))
                            .build())
                .collect(toList());


    customProperties =
        new PropertiesImpl.Builder()
            .name((String) prop.get(NAME))
            .type((String) prop.get(TYPE))
            .removed(Boolean.parseBoolean((String) prop.get(REMOVED)))
            .state((String) prop.get(STATE))
            .innerDisplayModelList(childDisplays)
            .build();
    if (prop.get(PUB_DATE) instanceof String) {
      customProperties.setPubDate(OffsetDateTime.parse((String) prop.get(PUB_DATE)));
    } else if (prop.get(PUB_DATE) instanceof OffsetDateTime) {
      customProperties.setPubDate((OffsetDateTime) prop.get(PUB_DATE));
    }

  }
}
