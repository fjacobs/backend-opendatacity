package com.dynacore.livemap.entity.jsonrepresentations.guidancesign;

import java.util.List;

import com.dynacore.livemap.entity.jsonrepresentations.Geometry;

public class ParkingguidanceDisplays {
//    "ParkingguidanceDisplay":[  
//                              {  
//                                 "Id":"000001091",
//                                 "OutputDescription":"VOL",
//                                 "Description":"A0201_VVX_P+R Arena",
//                                 "Type":"VVX",
//                                 "Output":"VOL"
//                              }
//                           ]
	public String Id;
	public String OutputDescription;
	public String Description;
	public String Type;
	public String Output;
	
	
	public String getId() {
		return Id;
	}
	public void setId(String id) {
		Id = id;
	}
	public String getOutputDescription() {
		return OutputDescription;
	}
	public void setOutputDescription(String outputDescription) {
		OutputDescription = outputDescription;
	}
	public String getDescription() {
		return Description;
	}
	public void setDescription(String description) {
		Description = description;
	}
	public String getType() {
		return Type;
	}
	public void setType(String type) {
		Type = type;
	}
	public String getOutput() {
		return Output;
	}
	public void setOutput(String output) {
		Output = output;
	}

	
	
}
