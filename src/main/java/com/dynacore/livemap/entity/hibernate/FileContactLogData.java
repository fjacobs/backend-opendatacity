package com.dynacore.livemap.entity.hibernate;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="fileContactLogData")
public class FileContactLogData {
	
		@Id
		@GeneratedValue
		private long id;
		private String Name;
		private String PubDate;
		private String State;
				
		public FileContactLogData(){};
		
		public FileContactLogData(String name, String pubDate,
				String state) {
			Name = name;
			PubDate = pubDate;
			setState(state);
			
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

		public String getState() {
			return State;
		}

		public void setState(String state) {
			State = state;
		}


}