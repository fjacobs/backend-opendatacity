package com.dynacore.livemap.entity.jsonrepresentations.guidancesign;


//For a full description see: https://open.data.amsterdam.nl/uploads/actuele_beschikbaarheid_parkeergarages/Beschrijving%20Dataset%20-%20Actuele%20beschikbaarheid%20Parkeergarages%20v2.pdf

import com.fasterxml.jackson.annotation.JsonProperty;

public class ParkingGuidanceDisplay {

//    "ParkingguidanceDisplay":[  
//                              {  
//                                 "Id":"000001091",
//                                 "OutputDescription":"VOL",
//                                 "Description":"A0201_VVX_P+R Arena",
//                                 "Type":"VVX",
//                                 "Output":"VOL"
//                              }
//                           ]
	private String id;
	private String description;

	//Display type.
	//Can be one of following three types:
	// VVX 			 (Can display ‘Vol’ or ‘Vrij’)
	// VVXNUMERIC	 (Can display ‘Vol’, ‘Vrij’ and/or available free parking places)
	// ROTATIONPANEL (The display text can be changed through rotating panel.)
	private String type;

	//Content of display output
	private String output;

	//Description of the content of field 'output'
	//In most cases this is equal to 'output'
	private String outputDescription;

	@JsonProperty("Id")
	public String getId() {
		return id;
	}
	@JsonProperty("OutputDescription")
	public String getOutputDescription() {
		return outputDescription;
	}
	@JsonProperty("Description")
	public String getDescription() {
		return description;
	}
	@JsonProperty("Type")
	public String getType() {
		return type;
	}
	@JsonProperty("Output")
	public String getOutput() {
		return output;
	}

}
