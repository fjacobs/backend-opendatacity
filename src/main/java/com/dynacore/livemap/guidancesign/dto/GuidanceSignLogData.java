package com.dynacore.livemap.guidancesign.dto;


import lombok.Data;
import org.hibernate.annotations.Immutable;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.List;

@Entity
@Immutable
@Table(name="guidanceSignLogData")
@Data
public class GuidanceSignLogData implements Serializable {
	
		@Id
		private long id;

		//Flatted from model.properties:
		private String Name;
		@Id
		private String PubDate;
		private String State;

		@OneToMany(mappedBy="guidanceSignLogData")
		private List<GuidanceDisplay> displayList;
		//-------------------------------

	public GuidanceSignLogData(long id, String name, String pubDate, String state, List<GuidanceDisplay> displayList) {
		this.id = id;
		Name = name;
		PubDate = pubDate;
		State = state;
		this.displayList = displayList;
	}



}