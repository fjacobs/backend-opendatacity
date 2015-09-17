package com.dynacore.livemap.service;

import com.dynacore.livemap.entity.hibernate.FileContactLogData;
import com.dynacore.livemap.entity.jsonrepresentations.filecontact.FeatureCollection;

public interface FileContactService {

	public void saveCollection(FeatureCollection fc);
	public FileContactLogData save(FileContactLogData fileContact);
	public FeatureCollection getProcessedJson();
	
}


