package com.dynacore.livemap.entity.hibernate;

import javax.persistence.*;
import java.time.LocalDateTime;


@Entity
@Table(name="parkinglogdata")
public class ParkingLogData {
	
		@Id
		@GeneratedValue(strategy = GenerationType.IDENTITY)
		private long id;
		private String name;
		private LocalDateTime pubDate;
		private String type;
		private String state;
		private int freeSpaceShort;
		private int freeSpaceLong;
		private int shortCapacity;
		private int longCapacity;
				
		public ParkingLogData(){}
		
		public ParkingLogData(String name, LocalDateTime pubDate, String type,  String state, int freeSpaceShort,
							  int freeSpaceLong, int shortCapacity, int longCapacity) {
			this.name = name;
			this.pubDate = pubDate;
			this.type = type;
			this.state = state;
			this.freeSpaceShort = freeSpaceShort;
			this.freeSpaceLong = freeSpaceLong;
			this.shortCapacity = shortCapacity;
			this.longCapacity = longCapacity;
		}
		
		public long getId() {
			return id;
		}
		public void setId(long id) {
			this.id = id;
		}

		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public LocalDateTime getPubDate() {
			return pubDate;
		}
		public void setPubDate(LocalDateTime pubDate) {
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
		public int getFreeSpaceShort() {
			return freeSpaceShort;
		}
		public void setFreeSpaceShort(int freeSpaceShort) {
			this.freeSpaceShort = freeSpaceShort;
		}
		public int getFreeSpaceLong() {
			return freeSpaceLong;
		}
		public void setFreeSpaceLong(int freeSpaceLong) {
			this.freeSpaceLong = freeSpaceLong;
		}
		public int getShortCapacity() {
			return shortCapacity;
		}
		public void setShortCapacity(int shortCapacity) {
			this.shortCapacity = shortCapacity;
		}
		public int getLongCapacity() {
			return longCapacity;
		}
		public void setLongCapacity(int longCapacity) {	this.longCapacity = longCapacity; }
}
