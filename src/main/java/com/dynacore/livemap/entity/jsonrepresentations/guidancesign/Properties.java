package com.dynacore.livemap.entity.jsonrepresentations.guidancesign;

import java.util.List;

public class Properties {
	
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
		//XXX: Moet public anders null..
    public String Name;
    public String PubDate;
    public String Removed;
    public String Type;
    public String State;
    public List<ParkingguidanceDisplays> ParkingguidanceDisplay;
    
    public String getName() {
		return Name;
	}
	public void setName(String name) {
		Name = name;
	}
	public String getPubDate() {
		return PubDate;
	}
	public void setPubDate(String pubDate) {
		PubDate = pubDate;
	}
	public String getRemoved() {
		return Removed;
	}
	public void setRemoved(String removed) {
		Removed = removed;
	}
	public String getType() {
		return Type;
	}
	public void setType(String type) {
		Type = type;
	}
	public String getState() {
		return State;
	}
	public void setState(String state) {
		State = state;
	}
	public List<ParkingguidanceDisplays> getParkingguidanceDisplay() {
		return ParkingguidanceDisplay;
	}
	public void setParkingguidanceDisplay(
			List<ParkingguidanceDisplays> parkingguidanceDisplay) {
		ParkingguidanceDisplay = parkingguidanceDisplay;
	}
    

				
}
