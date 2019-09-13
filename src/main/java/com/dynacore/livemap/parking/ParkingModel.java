package com.dynacore.livemap.parking;

import com.dynacore.livemap.common.model.Feature;
import com.dynacore.livemap.common.model.Geometry;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;
import java.util.Map;

public class ParkingModel implements Feature {

	@JsonProperty("Id")
	private String id;
	@JsonProperty("type")
	private String type;
	@JsonProperty("geometry")
	private Geometry geometry;
	@JsonProperty("properties")
	private Properties properties;

	class Properties {
		@JsonProperty("name")
		private String name;
		@JsonProperty("pubDate")
		private LocalDateTime pubDate;
		@JsonProperty("type")
		private String type;
		@JsonProperty("state")
		private String state;
		@JsonProperty("freeSpaceShort")
		private int freeSpaceShort;
		@JsonProperty("freeSpaceLong")
		private int freeSpaceLong;
		@JsonProperty("shortCapacity")
		private int shortCapacity;
		@JsonProperty("longCapacity")
		private int longCapacity;
		@JsonProperty("percentage")
		private int percentage = -1;

		Properties(String name, String pubDate, String type, String state,
				   String freeSpaceShort, String freeSpaceLong, String shortCapacity,
				   String longCapacity) {

				this.name = name;
				this.pubDate = LocalDateTime.parse(pubDate.substring(0, pubDate.length() - 1));
				this.type = type;
				this.state = state;
				this.freeSpaceShort= Integer.parseInt("0"+ freeSpaceShort);
				this.freeSpaceLong = Integer.parseInt("0"+ freeSpaceLong);
				this.shortCapacity = Integer.parseInt("0"+ shortCapacity);
				this.longCapacity  = Integer.parseInt("0"+ longCapacity);
		}
    }


	@JsonProperty("properties")
	private void unpackNested(Map<String,Object> prop) {

		properties = new Properties( (String) prop.get("Name"),
									 	 (String)prop.get("PubDate"),
								 		 (String)prop.get("Type"),
								 	  	 (String)prop.get("State"),
									 	 (String)prop.get("FreeSpaceShort"),
									 	 (String)prop.get("FreeSpaceLong"),
									     (String)prop.get("ShortCapacity"),
									 	 (String)prop.get("LongCapacity")
								   	   );

	}

	public void setId(String id) {
		this.id = id;
	}
	String getId() {
		return id;
	}
	public void setGeometry(Geometry geometry) {
		this.geometry = geometry;
	}
	public Geometry getGeometry() {
		return geometry;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getType() {
		return type;
	}

	@JsonIgnore
	String getName() {
		return properties.name;
	}
	@JsonIgnore
	LocalDateTime getPubDate() {
		return properties.pubDate;
	}
	@JsonIgnore
	public String getPropType() {
		return properties.type;
	}
	@JsonIgnore
	String getState() {
		return properties.state;
	}
	@JsonIgnore
	int getFreeSpaceShort() {
		return Integer.parseInt("0"+ properties.freeSpaceShort);
	}
	@JsonIgnore
	int getFreeSpaceLong() {
		return Integer.parseInt("0"+properties.freeSpaceLong);
	}
	@JsonIgnore
	int getShortCapacity() {
		return Integer.parseInt("0"+properties.shortCapacity);
	}
	@JsonIgnore
	int getLongCapacity() {
		return Integer.parseInt("0"+properties.longCapacity);
	}
    //Custom:
    @JsonIgnore
	void setPercentage(int percentage) {
        properties.percentage = percentage;
    }
}
