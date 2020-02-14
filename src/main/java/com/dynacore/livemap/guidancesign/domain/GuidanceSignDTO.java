package com.dynacore.livemap.guidancesign.domain;

import com.dynacore.livemap.core.model.TrafficMapDTO;

import java.time.OffsetDateTime;
import java.util.List;


public class GuidanceSignDTO extends TrafficMapDTO {

  private Boolean removed;
  private String state;
  private List<InnerDisplayDTO> innerDisplayList;

  @Override
  public void setId(String id) {
  }

  @Override
  public String getId() {
    return null;
  }

  @Override
  public OffsetDateTime getPubDate() {
    return null;
  }
  @Override
  public void setPubDate(OffsetDateTime pubDate) {
  }

}
