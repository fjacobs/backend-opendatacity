package com.dynacore.livemap.parking;


import com.dynacore.livemap.common.model.FeatureCollection;
import com.dynacore.livemap.common.repo.JpaRepository;
import com.dynacore.livemap.common.service.TrafficObject;
import com.dynacore.livemap.tools.HttpGeoJsonSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service("parkingPlaceService")
public class ParkingService implements TrafficObject<FeatureCollection<ParkingModel>> {
	
	private JpaRepository<ParkingDTO> parkingRepo;

	private static final int POLLING_INITIAL_DELAY = 0;
	private static final int POLLING_UPDATE_INTERVAL = 60;

	private static final String DATA_SOURCE_URL_KEY = "http://opd.it-t.nl/Data/parkingdata/v1/amsterdam/ParkingLocation.json";

	private FeatureCollection<ParkingModel> liveData;

	@Autowired
	public ParkingService(JpaRepository<ParkingDTO> parkingRepo)  {
		this.parkingRepo = parkingRepo;
		ScheduledExecutorService exec = Executors.newSingleThreadScheduledExecutor();
		exec.scheduleAtFixedRate( () -> {
			HttpGeoJsonSerializer<FeatureCollection<ParkingModel>> httpGeoJsonSerializer = new HttpGeoJsonSerializer<>();
			try {
				liveData = httpGeoJsonSerializer.marshallFromUrl(DATA_SOURCE_URL_KEY, ParkingModel.class);
				saveCollection(liveData);
			}  catch (ResponseStatusException responseException){
				liveData.setErrorReport(responseException.getReason());
			}
		}, POLLING_INITIAL_DELAY, POLLING_UPDATE_INTERVAL, TimeUnit.SECONDS);
	}

	@Override
	public FeatureCollection<ParkingModel> getLiveData() {
		if(liveData!=null) {
			liveData.getFeatures().stream().forEach(ParkingService::setPercentage);
		} else {
			liveData = new FeatureCollection<ParkingModel>();
			liveData.setErrorReport("Error: Could not get data from " + DATA_SOURCE_URL_KEY);
		}

		return liveData;
	}

	private static void setPercentage(ParkingModel parking) {
		try {
			parking.setPercentage(((parking.getShortCapacity() - parking.getFreeSpaceShort()) * 100) / parking.getShortCapacity());
		} catch(ArithmeticException divideByZero) {
			parking.setPercentage(-1);
		}
	}

	@Override
	@Transactional
	public void saveCollection(FeatureCollection<ParkingModel> fc) {
        fc.getFeatures().forEach(parking -> parkingRepo.save(new ParkingDTO(parking.getId(),
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


