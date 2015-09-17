package com.dynacore.livemap.entity.jsonrepresentations.filecontact;

import java.util.List;

import com.dynacore.livemap.entity.jsonrepresentations.Geometry;

public class FileContact {

	private String Id;
	private String type;
	private Geometry geometry;
	private List<Property> properties;
	
	public Geometry getGeometry() {
		return geometry;
	}

	public String getId() {
		return Id;
	}

	public String getType() {
		return type;
	}

	public void setGeometry(Geometry geometry) {
		this.geometry = geometry;
	}

	public void setId(String id) {
		Id = id;
	}

	public void setType(String type) {
		this.type = type;
	}

	public List<Property> getProperties() {
		return properties;
	}

	public void setProperties(List<Property> properties) {
		this.properties = properties;
	}

}
