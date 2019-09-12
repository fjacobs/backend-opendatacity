package com.dynacore.livemap.service;


import com.dynacore.livemap.entity.hibernate.ParkingLogData;
import com.dynacore.livemap.repository.JpaRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dynacore.livemap.entity.jsonrepresentations.FeatureCollection;
import com.dynacore.livemap.entity.jsonrepresentations.parking.ParkingPlace;
import org.springframework.web.server.ResponseStatusException;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service("parkingPlaceService")
public class ParkingPlaceCollectorServiceImpl implements TrafficDataCollectorService<FeatureCollection<ParkingPlace>> {
	
	private JpaRepository<ParkingLogData> parkingRepo;

	private static final int POLLING_INITIAL_DELAY = 0;
	private static final int POLLING_UPDATE_INTERVAL = 60;

	private static final String DATA_SOURCE_URL_KEY = "http://opd.it-t.nl/Data/parkingdata/v1/amsterdam/ParkingLocation.json";

	private FeatureCollection<ParkingPlace> liveData;

	@Autowired
	public ParkingPlaceCollectorServiceImpl(JpaRepository<ParkingLogData> parkingRepo)  {
		this.parkingRepo = parkingRepo;
		ScheduledExecutorService exec = Executors.newSingleThreadScheduledExecutor();
		exec.scheduleAtFixedRate( () -> {
			RestMapper<FeatureCollection<ParkingPlace>> restMapper = new RestMapper<>();
			try {
				liveData = restMapper.marshallFromUrl(DATA_SOURCE_URL_KEY, ParkingPlace.class);
				saveCollection(liveData);
			}  catch (ResponseStatusException responseException){
				liveData.setErrorReport(responseException.getReason());
			}
		}, POLLING_INITIAL_DELAY, POLLING_UPDATE_INTERVAL, TimeUnit.SECONDS);
	}

	@Override
	public FeatureCollection<ParkingPlace> getLiveData() {
		if(liveData!=null) {
			liveData.getFeatures().stream().forEach(ParkingPlaceCollectorServiceImpl::setPercentage);
		} else {
			liveData = new FeatureCollection<ParkingPlace>();
			liveData.setErrorReport("Error: Could not get data from " + DATA_SOURCE_URL_KEY);
		}

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
	public void saveCollection(FeatureCollection<ParkingPlace> fc) {
        fc.getFeatures().forEach(parking -> parkingRepo.save(new ParkingLogData(parking.getId(),
																				parking.getName(),
																				parking.getPubDate(),
																				parking.getState(),
																				parking.getFreeSpaceShort(),
																				parking.getFreeSpaceLong(),
																				parking.getShortCapacity(),
																				parking.getLongCapacity()))
        );
	}
}


