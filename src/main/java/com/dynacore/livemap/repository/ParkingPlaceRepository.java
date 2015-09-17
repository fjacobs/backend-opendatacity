package com.dynacore.livemap.repository;

import com.dynacore.livemap.entity.hibernate.ParkingLogData;

public interface ParkingPlaceRepository {
	ParkingLogData save(ParkingLogData parkingLogData);
}