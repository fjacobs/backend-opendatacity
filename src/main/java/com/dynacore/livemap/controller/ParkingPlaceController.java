package com.dynacore.livemap.controller;

import com.dynacore.livemap.entity.jsonrepresentations.FeatureCollection;
import com.dynacore.livemap.entity.jsonrepresentations.parking.ParkingPlace;
import com.dynacore.livemap.service.TrafficDataCollectorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class ParkingPlaceController {

    TrafficDataCollectorService<FeatureCollection<ParkingPlace>> parkingPlaceService;

    @Autowired
    public ParkingPlaceController(
            TrafficDataCollectorService<FeatureCollection<ParkingPlace>> parkingPlaceService) {
        this.parkingPlaceService = parkingPlaceService;
    }

    @RequestMapping(value = "getCustomParkingJson")
    @ResponseBody
    public FeatureCollection<ParkingPlace> getCustomParkingJson() {
        return parkingPlaceService.getLiveData();
    }
}
