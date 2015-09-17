package com.dynacore.livemap.entity.jsonrepresentations.parking;

import java.util.List;

public class FeatureCollection {
	private String type;
	private List<ParkingPlace> features;
		
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public List<ParkingPlace> getFeatures() {
		return features;
	}
	public void setFeatures(List<ParkingPlace> features) {
		this.features = features;
	} 
}
