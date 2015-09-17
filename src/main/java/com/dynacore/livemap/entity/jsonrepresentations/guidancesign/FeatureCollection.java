package com.dynacore.livemap.entity.jsonrepresentations.guidancesign;

import java.util.List;

public class FeatureCollection {
	private String type;
	private List<GuidanceSign> features;
		
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public List<GuidanceSign> getFeatures() {
		return features;
	}
	public void setFeatures(List<GuidanceSign> features) {
		this.features = features;
	} 
}
