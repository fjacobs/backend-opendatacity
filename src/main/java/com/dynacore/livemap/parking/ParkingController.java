package com.dynacore.livemap.parking;

import com.dynacore.livemap.common.model.FeatureCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class ParkingController {

    private ParkingService parkingPlaceService;
    private final Logger logger = LoggerFactory.getLogger(ParkingController.class);


    @Autowired
    public ParkingController(ParkingService parkingPlaceService) {
        this.parkingPlaceService = parkingPlaceService;
    }

    @CrossOrigin(origins = "http://localhost:63342")
    @GetMapping(value = "getCustomParkingJson")
    @ResponseBody
    public FeatureCollection<ParkingModel> getCustomParkingJson() {
        return parkingPlaceService.getLastUpdate();
    }

}
