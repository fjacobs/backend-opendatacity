package com.dynacore.livemap.entity.jsonrepresentations.parking;

import com.dynacore.livemap.entity.jsonrepresentations.Feature;
import com.dynacore.livemap.entity.jsonrepresentations.Geometry;
import com.dynacore.livemap.entity.jsonrepresentations.Properties;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;
import java.util.Map;

public class ParkingPlace implements Feature {

	@JsonProperty("Id")
	private String id;
	@JsonProperty("type")
	private String type;
	@JsonProperty("geometry")
	private Geometry geometry;
	@JsonProperty("properties")
	private ParkingPlaceProperties properties;

	public void setId(String id) {
		this.id = id;
	}
	public String getId() {
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

	static class ParkingPlaceProperties implements Properties {
		private String name;
		private String pubDate;
		private String type;
		private String state;
		private String freeSpaceShort;
		private String freeSpaceLong;
		private String shortCapacity;
		private String longCapacity;
		private int percentage = -1;

		public ParkingPlaceProperties( String name, String pubDate, String type, String state,
							String freeSpaceShort, String freeSpaceLong, String shortCapacity,
							String longCapacity) {
			this.name = name;
			this.pubDate = pubDate;
			this.type = type;
			this.state = state;
			this.freeSpaceShort = freeSpaceShort;
			this.freeSpaceLong = freeSpaceLong;
			this.shortCapacity = shortCapacity;
			this.longCapacity = longCapacity;
		}


        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getPubDate() {
            return pubDate;
        }

        public void setPubDate(String pubDate) {
            this.pubDate = pubDate;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getState() {
            return state;
        }

        public void setState(String state) {
            this.state = state;
        }

        public String getFreeSpaceShort() {
            return freeSpaceShort;
        }

        public void setFreeSpaceShort(String freeSpaceShort) {
            this.freeSpaceShort = freeSpaceShort;
        }

        public String getFreeSpaceLong() {
            return freeSpaceLong;
        }

        public void setFreeSpaceLong(String freeSpaceLong) {
            this.freeSpaceLong = freeSpaceLong;
        }

        public String getShortCapacity() {
            return shortCapacity;
        }

        public void setShortCapacity(String shortCapacity) {
            this.shortCapacity = shortCapacity;
        }

        public String getLongCapacity() {
            return longCapacity;
        }

        public void setLongCapacity(String longCapacity) {
            this.longCapacity = longCapacity;
        }

        public int getPercentage() {
            return percentage;
        }

        public void setPercentage(int percentage) {
            this.percentage = percentage;
        }
    }


	@SuppressWarnings("unchecked")
	@JsonProperty("properties")
	private void unpackNested(Map<String,Object> propertiesPacked) {

		properties = new ParkingPlaceProperties( (String)propertiesPacked.get("Name"),
									 (String)propertiesPacked.get("PubDate"),
								 	 (String)propertiesPacked.get("Type"),
								 	 (String)propertiesPacked.get("State"),
									 (String)propertiesPacked.get("FreeSpaceShort"),
									 (String)propertiesPacked.get("FreeSpaceLong"),
									 (String)propertiesPacked.get("ShortCapacity"),
									 (String)propertiesPacked.get("LongCapacity")
								   );

	}

	@JsonIgnore
	public String getName() {
		return properties.name;
	}
	@JsonIgnore
	public LocalDateTime getPubDate() {
		return LocalDateTime.parse(properties.pubDate.substring(0, properties.pubDate.length() - 1));
	}
	@JsonIgnore
	public String getPropType() {
		return properties.type;
	}
	@JsonIgnore
	public String getState() {
		return properties.state;
	}
	@JsonIgnore
	public int getFreeSpaceShort() {
		return Integer.parseInt("0"+ properties.freeSpaceShort);
	}
	@JsonIgnore
	public int getFreeSpaceLong() {
		return Integer.parseInt("0"+properties.freeSpaceLong);
	}
	@JsonIgnore
	public int getShortCapacity() {
		return Integer.parseInt("0"+properties.shortCapacity);
	}
	@JsonIgnore
	public int getLongCapacity() {
		return Integer.parseInt("0"+properties.longCapacity);
	}
    //Custom:
    @JsonIgnore
    public void setPercentage(int percentage) {
        properties.percentage = percentage;
    }
}
