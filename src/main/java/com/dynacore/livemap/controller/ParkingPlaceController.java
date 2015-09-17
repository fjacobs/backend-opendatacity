package com.dynacore.livemap.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.dynacore.livemap.entity.jsonrepresentations.parking.FeatureCollection;
import com.dynacore.livemap.service.ParkingPlaceService;

@Controller
public class ParkingPlaceController {

	@Autowired 
	ParkingPlaceService parkingPlaceService;
	FeatureCollection top;
	
	public ParkingPlaceController() { }
	
	@RequestMapping(value = "getCustomParkingJson")
	@ResponseBody
	public FeatureCollection getCustomParkingJson() {
		return parkingPlaceService.getProcessedJson();
	}
 
}
