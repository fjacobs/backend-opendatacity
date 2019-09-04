package com.dynacore.livemap.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.dynacore.livemap.entity.jsonrepresentations.FeatureCollection;
import com.dynacore.livemap.service.TrafficDataCollectorService;
import com.dynacore.livemap.entity.jsonrepresentations.guidancesign.GuidanceSign;

@Controller
public class GuidanceSignController {
	
	@Autowired
	TrafficDataCollectorService<FeatureCollection<GuidanceSign>> guidanceSignService;

	public GuidanceSignController() { }
	
	@RequestMapping(value = "getCustomGuidanceSignJson")
	@ResponseBody
	public FeatureCollection<GuidanceSign> getCustomJson() {
		return guidanceSignService.getLiveData();
	}
 
}
