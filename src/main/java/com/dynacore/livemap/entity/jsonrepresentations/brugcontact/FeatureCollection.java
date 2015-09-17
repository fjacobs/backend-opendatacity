package com.dynacore.livemap.entity.jsonrepresentations.brugcontact;

import java.util.List;

public class FeatureCollection {
	private String type;
	private List<BrugContact> features;
		
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public List<BrugContact> getFeatures() {
		return features;
	}
	public void setFeatures(List<BrugContact> features) {
		this.features = features;
	} 
}
