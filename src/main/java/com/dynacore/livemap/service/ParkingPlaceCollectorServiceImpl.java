package com.dynacore.livemap.service;


import com.dynacore.livemap.entity.hibernate.ParkingLogData;
import com.dynacore.livemap.repository.JpaRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dynacore.livemap.entity.jsonrepresentations.GeoJsonCollection;
import com.dynacore.livemap.entity.jsonrepresentations.parking.ParkingPlace;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service("parkingPlaceService")
public class ParkingPlaceCollectorServiceImpl implements TrafficDataCollectorService<GeoJsonCollection<ParkingPlace>> {
	
	private JpaRepository<ParkingLogData> parkingRepo;

	private static final int POLLING_INITIAL_DELAY = 0;
	private static final int POLLING_UPDATE_INTERVAL = 60;

	@Value("${vialis.amsterdam.parkingplace.jsonurl}")
	private String DATA_SOURCE_URL_KEY;

	private GeoJsonCollection<ParkingPlace> liveData;

	@Autowired
	public ParkingPlaceCollectorServiceImpl(JpaRepository<ParkingLogData> parkingRepo) {
		this.parkingRepo = parkingRepo;
		ScheduledExecutorService exec = Executors.newSingleThreadScheduledExecutor();
		exec.scheduleAtFixedRate( () -> {
			RestMapper<GeoJsonCollection<ParkingPlace>> restMapper = new RestMapper<>();
			liveData = restMapper.marshallFromUrl(DATA_SOURCE_URL_KEY, ParkingPlace.class);
			saveCollection(liveData);
		}, POLLING_INITIAL_DELAY, POLLING_UPDATE_INTERVAL, TimeUnit.SECONDS);

	}


	@Override
	public GeoJsonCollection<ParkingPlace> getLiveData() {
		liveData.getFeatures().stream().forEach(ParkingPlaceCollectorServiceImpl::setPercentage);
		return liveData;
	}

	private static void setPercentage(ParkingPlace parking) {
		try {
			parking.setPercentage(((parking.getShortCapacity() - parking.getFreeSpaceShort()) * 100) / parking.getShortCapacity());
		} catch(ArithmeticException divideByZero) {
			parking.setPercentage(-1);
		}
	}

	@Override
	@Transactional
	public void saveCollection(GeoJsonCollection<ParkingPlace> fc) {
        fc.getFeatures().forEach(parking -> parkingRepo.save(new ParkingLogData(parking.getId(),
																				parking.getName(),
																				parking.getPubDate(),
																				parking.getType(),
																				parking.getState(),
																				parking.getFreeSpaceShort(),
																				parking.getFreeSpaceLong(),
																				parking.getShortCapacity(),
																				parking.getLongCapacity()))
        );
	}
}


