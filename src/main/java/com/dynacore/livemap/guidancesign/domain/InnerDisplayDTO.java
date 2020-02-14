package com.dynacore.livemap.guidancesign.domain;

import com.dynacore.livemap.core.model.TrafficMapDTO;
import lombok.NoArgsConstructor;

import static com.dynacore.livemap.guidancesign.domain.GuidanceSignFeatureImpl.*;

@NoArgsConstructor
public class InnerDisplayDTO extends TrafficMapDTO {

  public String getOutputDescription() {
    return (String) get(OUTPUT_DESCRIPTION);
  }

  public void setOutputDescription(String outputDescription) {
    put(OUTPUT_DESCRIPTION, outputDescription);
  }

  public String getDescription() {
    return (String) get(DESCRIPTION);
  }

  public void setDescription(String description) {
    put(DESCRIPTION, description);
  }

  public String getOutput() {
    return (String) get(OUTPUT);
  }

  public void setOutput(String output) {
    put(OUTPUT, output);
  }
}
