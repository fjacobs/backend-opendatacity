package com.dynacore.livemap.entity.jsonrepresentations.filecontact;

import java.util.List;

public class FeatureCollection {
	private String type;
	private List<FileContact> features;
		
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public List<FileContact> getFeatures() {
		return features;
	}
	public void setFeatures(List<FileContact> features) {
		this.features = features;
	} 
}
