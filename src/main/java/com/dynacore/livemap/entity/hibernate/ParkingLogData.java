package com.dynacore.livemap.entity.hibernate;

import javax.persistence.*;

@Entity
@Table(name="parkingLogData2")
public class ParkingLogData {
	
		@Id
		@GeneratedValue(strategy = GenerationType.IDENTITY)
		private long id;
		private String Name;
		private String PubDate;
		private String Type;
		private String State;
		private int FreeSpaceShort;
		private int FreeSpaceLong;
		private int ShortCapacity;
		private int LongCapacity;
				
		public ParkingLogData(){}
		
		public ParkingLogData(String name, String pubDate, String type,  String state, int freeSpaceShort,
							  int freeSpaceLong, int shortCapacity, int longCapacity) {
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
		public int getFreeSpaceShort() {
			return FreeSpaceShort;
		}
		public void setFreeSpaceShort(int freeSpaceShort) {
			FreeSpaceShort = freeSpaceShort;
		}
		public int getFreeSpaceLong() {
			return FreeSpaceLong;
		}
		public void setFreeSpaceLong(int freeSpaceLong) {
			FreeSpaceLong = freeSpaceLong;
		}
		public int getShortCapacity() {
			return ShortCapacity;
		}
		public void setShortCapacity(int shortCapacity) {
			ShortCapacity = shortCapacity;
		}
		public int getLongCapacity() {
			return LongCapacity;
		}
		public void setLongCapacity(int longCapacity) {
			LongCapacity = longCapacity;
		}				
}
