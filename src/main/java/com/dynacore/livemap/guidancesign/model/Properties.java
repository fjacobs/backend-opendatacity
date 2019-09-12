package com.dynacore.livemap.guidancesign.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;
import java.util.List;

//input
//    "Name":"A1006_A010_23,550-Re-1_P+R Bos en Lommer",
//    "PubDate":"2015-05-15T15:08:21.000Z",
//    "Removed":"false",
//    "Type":"guidancesign",
//    "State":"ok",
//    "ParkingguidanceDisplay":[
//       {
//          "Id":"000001094",
//          "OutputDescription":"VOL",
//          "Description":"A1006_VVX_P+R Bos en Lommer",
//          "Type":"VVX",
//          "Output":"VOL"
//       }
//    ]


public class Properties {

	@JsonProperty("name")
	String name;
	@JsonProperty("pubDate")
	LocalDateTime pubDate;
	@JsonProperty("type")
	String type;
	@JsonProperty("state")
	String state;
	@JsonProperty("removed")
	String removed;
	@JsonProperty("ParkingguidanceDisplay")
	List<ParkingGuidanceDisplay> parkingGuidanceDisplayList;

	Properties(String name, String pubDate, String type, String state, String removed, List<ParkingGuidanceDisplay> parkingGuidanceDisplayList) {
		this.name = name;
		this.pubDate = LocalDateTime.parse(pubDate.substring(0, pubDate.length() - 1));
		this.type = type;
		this.state = state;
		this.removed = removed;
		this.parkingGuidanceDisplayList = parkingGuidanceDisplayList;
	}

	public List<ParkingGuidanceDisplay> getParkingGuidanceDisplayList() {
		return parkingGuidanceDisplayList;
	}
}
