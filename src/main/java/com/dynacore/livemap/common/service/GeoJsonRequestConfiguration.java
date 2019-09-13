package com.dynacore.livemap.common.service;

public class GeoJsonRequestConfiguration {
    private String url;
    private int initialDelay;
    private int requestInterval;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getInitialDelay() {
        return initialDelay;
    }

    public void setInitialDelay(int initialDelay) {
        this.initialDelay = initialDelay;
    }

    public int getRequestInterval() {
        return requestInterval;
    }

    public void setRequestInterval(int requestInterval) {
        this.requestInterval = requestInterval;
    }
}
