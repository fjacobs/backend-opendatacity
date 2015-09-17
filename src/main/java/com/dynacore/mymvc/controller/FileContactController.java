package com.dynacore.mymvc.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.dynacore.livemap.entity.jsonrepresentations.filecontact.FeatureCollection;
import com.dynacore.livemap.service.FileContactService;

@Controller
public class FileContactController {

	@Autowired 
	FileContactService fileContactService;
	FeatureCollection top;
	
	public FileContactController() { }
	
	@RequestMapping(value = "getCustomFileContactJson")
	@ResponseBody
	public FeatureCollection getCustomParkingJson() {
		System.out.println("hello");
		return fileContactService.getProcessedJson();
	}
 
}
