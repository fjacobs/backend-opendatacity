package com.dynacore.livemap.service;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.dynacore.livemap.entity.hibernate.ParkingLogData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import com.dynacore.livemap.repository.ParkingPlaceRepository;

import com.dynacore.livemap.entity.jsonrepresentations.GeoJsonCollection;
import com.dynacore.livemap.entity.jsonrepresentations.parking.ParkingPlace;

@Service("parkingPlaceService")
public class ParkingPlaceCollectorServiceImpl implements TrafficDataCollectorService<GeoJsonCollection<ParkingPlace>> {
	
	@Autowired 
	private ParkingPlaceRepository parkingPlaceRepository;
	private GeoJsonCollection<ParkingPlace> parkingJson;
	private int updateInterval = 60;

	public ParkingPlaceCollectorServiceImpl() {
		ScheduledExecutorService exec = Executors.newSingleThreadScheduledExecutor();
		exec.scheduleAtFixedRate(() -> {
		  RestTemplate restTemplate = createRestClient();
		  try {
		          parkingJson = restTemplate.exchange( getDataSourceUrl(ParkingPlace.class.getSimpleName()),
												   	   HttpMethod.GET,	null,
					  							 	   new ParameterizedTypeReference<GeoJsonCollection<ParkingPlace>>() {}).getBody();
			  	  saveCollection(parkingJson);
				  customizeJson(parkingJson); //Customize Json for frontend.

		  } catch (Exception e) {
			  e.printStackTrace();
		  }
		}, 0, updateInterval, TimeUnit.SECONDS);
	}

	private static void calculatePercentage(ParkingPlace parking) {
		try {
			parking.setPercentage(((parking.getShortCapacity() - parking.getFreeSpaceShort()) * 100) / parking.getShortCapacity());
		} catch(ArithmeticException divideByZero) {
			parking.setPercentage(-1);
		}
	}

	private GeoJsonCollection<ParkingPlace> customizeJson(GeoJsonCollection<ParkingPlace> featureColl) {
		featureColl.getFeatures().stream()
							 	 .forEach(ParkingPlaceCollectorServiceImpl::calculatePercentage);
		return featureColl;
	}

	@Override
	public GeoJsonCollection<ParkingPlace> getProcessedJson() {
		return parkingJson;
	}
		
	@Override
	@Transactional
	public void saveCollection(GeoJsonCollection<ParkingPlace> fc) {
		//TODO: Check datetime to see if logdata is already written
        fc.getFeatures().forEach(parking -> parkingPlaceRepository.save(new ParkingLogData(parking.getName(),
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


