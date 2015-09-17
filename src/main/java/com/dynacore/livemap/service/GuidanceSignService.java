package com.dynacore.livemap.service;

import com.dynacore.livemap.entity.hibernate.GuidanceSignLogData;
import com.dynacore.livemap.entity.jsonrepresentations.guidancesign.FeatureCollection;

public interface GuidanceSignService {

	public void saveCollection(FeatureCollection fc);
	public GuidanceSignLogData save(GuidanceSignLogData guidanceSign);
	public FeatureCollection getProcessedJson();
	
}


