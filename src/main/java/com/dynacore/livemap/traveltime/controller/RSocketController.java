package com.dynacore.livemap.traveltime.controller;

import java.time.Duration;

import org.geojson.Feature;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

import com.dynacore.livemap.traveltime.service.TravelTimeService;

import reactor.core.publisher.Flux;

@Controller
public class RSocketController {

    private final TravelTimeService service;

    public RSocketController(TravelTimeService service) {
        this.service = service;
    }

    @MessageMapping("traveltime-message")
    public Flux<Feature> getRoadSubscription() {
        System.err.println("Enter getRoadSubscription..");
        return service.getFeatures();
    }

}
