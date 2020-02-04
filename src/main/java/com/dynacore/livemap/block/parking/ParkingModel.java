package com.dynacore.livemap.block.parking;

import com.dynacore.livemap.core.model.Feature;
import com.dynacore.livemap.core.model.Properties;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Getter @Setter
public class ParkingModel extends Feature {

	@JsonProperty("Id")
	private void createUUID(String id) {
		this.id = UUID.nameUUIDFromBytes(id.getBytes());
	}

	@JsonIgnore
	String getName() {
		return properties.getName();
	}
	@JsonIgnore
	LocalDateTime getPubDate() {
		return properties.getPubDate();
	}
	@JsonIgnore
	public String getPropType() {
		return properties.getType();
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
		return Integer.parseInt("0"+ properties.freeSpaceLong);
	}
	@JsonIgnore
	int getShortCapacity() {
		return Integer.parseInt("0"+ properties.shortCapacity);
	}
	@JsonIgnore
	int getLongCapacity() {
		return Integer.parseInt("0"+ properties.longCapacity);
	}
    //Custom:
    @JsonIgnore
	void setPercentage(int percentage) {
		properties.percentage = percentage;
    }

	private PropertiesImpl properties;
	@JsonProperty("properties")
	private void unpackNested(Map<String,Object> prop) {

		properties = new PropertiesImpl( (String) prop.get("Name"),
				(String)prop.get("PubDate"),
				(String)prop.get("Type"),
				(String)prop.get("State"),
				(String)prop.get("FreeSpaceShort"),
				(String)prop.get("FreeSpaceLong"),
				(String)prop.get("ShortCapacity"),
				(String)prop.get("LongCapacity")
		);
	}

	class PropertiesImpl extends Properties {
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

		PropertiesImpl(String name, String pubDate, String type, String state,
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

}
