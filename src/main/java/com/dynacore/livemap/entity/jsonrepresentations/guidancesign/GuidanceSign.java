package com.dynacore.livemap.entity.jsonrepresentations.guidancesign;

import com.dynacore.livemap.entity.jsonrepresentations.Feature;
import com.dynacore.livemap.entity.jsonrepresentations.Geometry;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class GuidanceSign implements Feature {

	@JsonProperty("Id")
	private String id;
	@JsonProperty("type")
	private String type;
	@JsonProperty("geometry")
	private Geometry geometry;
	@JsonProperty("properties")
	private Properties properties;

	//Marshall properties and change to camel casing:
	@JsonProperty("properties")
	private void unpackNested(Map<String,Object> prop) {
		properties = new Properties( (String) prop.get("Name"),
				(String)prop.get("PubDate"),
				(String)prop.get("Type"),
				(String)prop.get("State"),
				(String)prop.get("Removed"),
				(List<ParkingGuidanceDisplay>)prop.get("ParkingguidanceDisplay"));
	}


	public Geometry getGeometry() {
		return geometry;
	}
	public String getId() {
		return id;
	}
	public Properties getProperties() {
		return properties;
	}
	public String getType() {
		return type;
	}

	@JsonIgnore
	public String getName() { return properties.name; }
	@JsonIgnore
	public LocalDateTime getPubDate() {	return properties.pubDate; }
	@JsonIgnore
	public String getPropType() {return properties.type; }
	@JsonIgnore
	public String getRemoved() { return properties.removed;	}
	@JsonIgnore
	public String getState() { return properties.state; }
	@JsonIgnore
	public List<ParkingGuidanceDisplay> getDisplayList() {return properties.parkingGuidanceDisplayList;}
	@JsonIgnore
	public void setParkingGuidanceDisplay(List<ParkingGuidanceDisplay> parkingGuidanceDisplay) {properties.parkingGuidanceDisplayList = parkingGuidanceDisplay;}
}
