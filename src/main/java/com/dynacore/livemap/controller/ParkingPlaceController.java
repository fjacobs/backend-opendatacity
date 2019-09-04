package com.dynacore.livemap.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.dynacore.livemap.entity.jsonrepresentations.FeatureCollection;
import com.dynacore.livemap.entity.jsonrepresentations.parking.ParkingPlace;
import com.dynacore.livemap.service.TrafficDataCollectorService;

@Controller
public class ParkingPlaceController {

	@Autowired
	TrafficDataCollectorService<FeatureCollection<ParkingPlace>> parkingPlaceService;

	public ParkingPlaceController() { }
	
	@RequestMapping(value = "getCustomParkingJson")
	@ResponseBody
	public FeatureCollection<ParkingPlace> getCustomParkingJson() {
		return parkingPlaceService.getLiveData();
	}

}
