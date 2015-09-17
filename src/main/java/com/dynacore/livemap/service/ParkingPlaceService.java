package com.dynacore.livemap.service;

import com.dynacore.livemap.entity.hibernate.ParkingLogData;
import com.dynacore.livemap.entity.jsonrepresentations.parking.FeatureCollection;

public interface ParkingPlaceService {

	public void saveCollection(FeatureCollection fc);
	public ParkingLogData save(ParkingLogData parkingPlace);
	public FeatureCollection getProcessedJson();
	
}


