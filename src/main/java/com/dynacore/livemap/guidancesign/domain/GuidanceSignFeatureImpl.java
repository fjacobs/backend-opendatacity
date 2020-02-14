package com.dynacore.livemap.guidancesign.domain;

import com.dynacore.livemap.core.model.TrafficFeatureImpl;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.ToString;
import org.geojson.Feature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

import static java.util.stream.Collectors.toList;

@ToString
public class GuidanceSignFeatureImpl extends TrafficFeatureImpl {

  Logger log = LoggerFactory.getLogger(TrafficFeatureImpl.class);

  private static final String PUB_DATE = "PubDate"; // Different suppliers use different pubdates
  private static final String TYPE = "Type";
  private static final String STATE = "State";
  private static final String REMOVED = "Removed";

  // Inner displays:
  public static final String INNER_LIST_KEY = "ParkingguidanceDisplay";
  public static final String DESCRIPTION = "Description";
  public static final String OUTPUT = "Output";
  public static final String OUTPUT_DESCRIPTION = "OutputDescription";

  @JsonProperty("ParkingguidanceDisplay")
  List<InnerDisplayModel> innerDisplayModelList;

  public GuidanceSignFeatureImpl() {
    feature = new Feature();
    innerDisplayModelList = new ArrayList<>();
  }

  // Import external:
  public GuidanceSignFeatureImpl(Feature feature) {
    super(feature, PUB_DATE);
    importInnerDisplayList(feature.getProperties());

    try {
      if (!feature.getProperties().containsKey(TYPE)) throw new NoSuchElementException(NAME);
      if (!feature.getProperties().containsKey(REMOVED)) throw new NoSuchElementException(REMOVED);
      if (!feature.getProperties().containsKey(STATE)) throw new NoSuchElementException(NAME);
    } catch (NoSuchElementException e) {
      log.warn("Did not find property while importing: " + e.getMessage());
    }
  }

  @JsonIgnore
  public Boolean getRemoved() {
    Boolean removed;
    if (feature.getProperty(REMOVED) instanceof Boolean) {
      removed = feature.getProperty(REMOVED);
    } else {
      removed = Boolean.parseBoolean(feature.getProperty(REMOVED));
    }
    return removed;
  }

  public void setRemoved(Boolean state) {
    feature.setProperty(STATE, state);
  }

  @JsonIgnore
  public String getState() {
    return (String) feature.getProperty(STATE);
  }

  public void setState(String state) {
    feature.setProperty(STATE, state);
  }

  public void setType(String type) {
    feature.setProperty(TYPE, type);
  }

  @JsonIgnore
  public String getType() {
    return feature.getProperty(TYPE);
  }

  public List<InnerDisplayModel> getInnerDisplays() {
    return innerDisplayModelList;
  }

  @JsonProperty("properties")
  public void importInnerDisplayList(Map<String, Object> prop) throws IllegalStateException {
    innerDisplayModelList =
        ((ArrayList<LinkedHashMap<String, String>>) prop.get(INNER_LIST_KEY))
            .stream()
                .map(
                    dispMap ->
                        new InnerDisplayModel.Builder()
                            .id(dispMap.get(ID))
                            .description(dispMap.get(DESCRIPTION))
                            .output(dispMap.get(OUTPUT))
                            .outputDescription(dispMap.get(OUTPUT_DESCRIPTION))
                            .type(dispMap.get(TYPE))
                            .build())
                .collect(toList());
  }
}
