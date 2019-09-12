package com.dynacore.livemap.parking;

import com.dynacore.livemap.common.model.FeatureCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class ParkingController {

    ParkingService parkingPlaceService;

    @Autowired
    public ParkingController(ParkingService parkingPlaceService) {
        this.parkingPlaceService = parkingPlaceService;
    }

    @RequestMapping(value = "getCustomParkingJson")
    @ResponseBody
    public FeatureCollection<ParkingModel> getCustomParkingJson() {
        return parkingPlaceService.getLiveData();
    }
}
