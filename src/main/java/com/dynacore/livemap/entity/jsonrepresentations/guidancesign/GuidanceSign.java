package com.dynacore.livemap.entity.jsonrepresentations.guidancesign;

import com.dynacore.livemap.entity.jsonrepresentations.Geometry;

public class GuidanceSign {

	private String Id;
	private String type;
	private Geometry geometry;
	private Properties properties;
	
	public Geometry getGeometry() {
		return geometry;
	}

	public String getId() {
		return Id;
	}

	public Properties getProperties() {
		return properties;
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

	public void setProperties(Properties properties) {
		this.properties = properties;
	}

	public void setType(String type) {
		this.type = type;
	}

}
