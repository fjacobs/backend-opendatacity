package com.dynacore.livemap.entity.hibernate;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="parkingLogData2")
public class ParkingLogData {
	
		@Id
		@GeneratedValue
		private long id;
		private String Name;
		private String PubDate;
		private String Type;
		private String State;
		private String FreeSpaceShort;
		private String FreeSpaceLong;
		private String ShortCapacity;
		private String LongCapacity;
				
		public ParkingLogData(){};
		
		public ParkingLogData(String name, String pubDate, String type,  String state, String freeSpaceShort,
							  String freeSpaceLong, String shortCapacity, String longCapacity) {
			Name = name;
			PubDate = pubDate;
			Type = type;
			State = state;
			FreeSpaceShort = freeSpaceShort;
			FreeSpaceLong = freeSpaceLong;
			ShortCapacity = shortCapacity;
			LongCapacity = longCapacity;
		}
		
		public long getId() {
			return id;
		}

		public void setId(long id) {
			this.id = id;
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
