package com.dynacore.livemap.core.service;

import lombok.*;

import java.time.Duration;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ServiceConfiguration {
  private String url;
  private Duration initialDelay;
  private Duration requestInterval;
  private Duration elementDelay;

  @Getter(AccessLevel.NONE)
  private Boolean saveToDbEnabled;

  public Boolean isSaveToDbEnabled() {
    return saveToDbEnabled;
  }
}
