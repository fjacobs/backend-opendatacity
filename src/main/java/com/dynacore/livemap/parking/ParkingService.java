package com.dynacore.livemap.parking;

import com.dynacore.livemap.common.model.FeatureCollection;
import com.dynacore.livemap.common.repo.JpaRepository;
import com.dynacore.livemap.common.service.GeoJsonRequester;
import com.dynacore.livemap.common.tools.HttpGeoJsonSerializer;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


@Service("parkingPlaceService")
public class ParkingService implements GeoJsonRequester<FeatureCollection<ParkingModel>> {

    private JpaRepository<ParkingDTO> parkingRepo;
    private FeatureCollection<ParkingModel> getLastUpdate;
    private ParkingConfiguration config;

    public ParkingService(JpaRepository<ParkingDTO> parkingRepo, ParkingConfiguration config) {
        this.parkingRepo = parkingRepo;
        this.config = config;
        pollRequest();
    }

    private void pollRequest() {
        ScheduledExecutorService exec = Executors.newSingleThreadScheduledExecutor();
        exec.scheduleAtFixedRate( () -> {
            HttpGeoJsonSerializer<FeatureCollection<ParkingModel>> httpGeoJsonSerializer = new HttpGeoJsonSerializer<>();
            try {
                getLastUpdate = httpGeoJsonSerializer.marshallFromUrl(config.getUrl(), ParkingModel.class);
            //    saveCollection((getLastUpdate));
            } catch (ResponseStatusException responseException) {
                getLastUpdate.setErrorReport(responseException.getReason());
            }
        }, config.getInitialDelay(), config.getRequestInterval(), TimeUnit.SECONDS);
    }

    @Transactional
    public void saveCollection(FeatureCollection<ParkingModel> features) {
            features.getFeatures().forEach(parking -> parkingRepo.save(new ParkingDTO( parking.getId(),
			                                                                           parking.getName(),
																					   parking.getPubDate(),
																					   parking.getState(),
																					   parking.getFreeSpaceShort(),
																					   parking.getFreeSpaceLong(),
																					   parking.getShortCapacity(),
																					   parking.getLongCapacity()))
        );
    }

	private static void setPercentage(ParkingModel parking) {
		try {
			parking.setPercentage(((parking.getShortCapacity() - parking.getFreeSpaceShort()) * 100) / parking.getShortCapacity());
		} catch (ArithmeticException divideByZero) {
			parking.setPercentage(-1);
		}
	}

	@Override
    public FeatureCollection<ParkingModel> getLiveData() {
        if (getLastUpdate != null) {
            getLastUpdate.getFeatures().stream().forEach(ParkingService::setPercentage);
        } else {
            getLastUpdate = new FeatureCollection<>();
            getLastUpdate.setErrorReport("Error: Could not get data from " + config.getUrl());
        }

        return getLastUpdate;
    }

}
