package com.dynacore.livemap.service;

import com.dynacore.livemap.entity.hibernate.BrugContactLogData;
import com.dynacore.livemap.entity.jsonrepresentations.brugcontact.FeatureCollection;

public interface BrugContactService {

	public void saveCollection(FeatureCollection fc);
	public BrugContactLogData save(BrugContactLogData brugContact);
	public FeatureCollection getProcessedJson();
	
}


