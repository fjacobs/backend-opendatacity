package com.dynacore.livemap.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.dynacore.livemap.entity.jsonrepresentations.brugcontact.FeatureCollection;
import com.dynacore.livemap.service.BrugContactService;

@Controller
public class BrugContactController {

	@Autowired 
	BrugContactService brugContactService;
	FeatureCollection top;
	
	public BrugContactController() { 
	}
	
	@RequestMapping(value = "getCustomBrugContactJson")
	@ResponseBody
	public FeatureCollection getCustomBrugContactJson() {
		
		return brugContactService.getProcessedJson();
	}
 
}
