package com.dynacore.livemap.entity.jsonrepresentations.filecontact;

public class Property {
	//XXX: Moet public anders null.. FIX THIS
	public String Name;
	public String Timestamp;
	public String State;
	
	public String getTimestamp() {
		return Timestamp;
	}
	public void setTimestamp(String timestamp) {
		Timestamp = timestamp;
	}
	public String getName() {
		return Name;
	}
	public void setName(String name) {
		Name = name;
	}
	public String getState() {
		return State;
	}
	public void setState(String state) {
		State = state;
	}
}
