package com.dynacore.livemap.entity.jsonrepresentations.parking;

public class Properties {
	
	//XXX: Moet public anders null..
	public String Name;
	public String PubDate;
	public String Type;
	public String State;
	public String FreeSpaceShort;
	public String FreeSpaceLong;
	public String ShortCapacity;
	public String LongCapacity;
	
	public int Percentage;

	public int getPercentage() {
		return Percentage;
	}

	public void setPercentage(int percentage) {
		this.Percentage = percentage;
	}
	
	public String getName() {
		return Name;
	}
	public void setName(String name) {
		Name = name;
	}
	public String getPubDate() {
		return PubDate;
	}
	public void setPubDate(String pubDate) {
		PubDate = pubDate;
	}
	public String getType() {
		return Type;
	}
	public void setType(String type) {
		Type = type;
	}
	public String getState() {
		return State;
	}
	public void setState(String state) {
		State = state;
	}
	public String getFreeSpaceShort() {
		return FreeSpaceShort;
	}
	public void setFreeSpaceShort(String freeSpaceShort) {
		FreeSpaceShort = freeSpaceShort;
	}
	public String getFreeSpaceLong() {
		return FreeSpaceLong;
	}
	public void setFreeSpaceLong(String freeSpaceLong) {
		FreeSpaceLong = freeSpaceLong;
	}
	public String getShortCapacity() {
		return ShortCapacity;
	}
	public void setShortCapacity(String shortCapacity) {
		ShortCapacity = shortCapacity;
	}
	public String getLongCapacity() {
		return LongCapacity;
	}
	public void setLongCapacity(String longCapacity) {
		LongCapacity = longCapacity;
	}
				
}
