package com.dynacore.livemap.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.dynacore.livemap.entity.jsonrepresentations.guidancesign.FeatureCollection;
import com.dynacore.livemap.service.GuidanceSignService;

@Controller
public class GuidanceSignController {
	
	@Autowired 
	GuidanceSignService guidanceSignService;
	FeatureCollection top;
	
	public GuidanceSignController() { }
	
	@RequestMapping(value = "getCustomGuidanceSignJson")
	@ResponseBody
	public FeatureCollection getCustomJson() {
		return guidanceSignService.getProcessedJson();
	}
 
}
