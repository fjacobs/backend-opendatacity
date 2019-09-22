package com.dynacore.livemap.traveltime;

import org.geojson.FeatureCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class TravelTimeController {

    private TravelTimeService travelTimeService;
    private final Logger logger = LoggerFactory.getLogger(TravelTimeController.class);

    @Autowired
    public TravelTimeController(TravelTimeService travelTimeService) {
        this.travelTimeService = travelTimeService;
    }

    @CrossOrigin(origins = "http://localhost:63342")
    @GetMapping(value = "getTravelTime")
    @ResponseBody
    public FeatureCollection getCustomParkingJson() {
        return travelTimeService.getLastUpdate();
    }

}
