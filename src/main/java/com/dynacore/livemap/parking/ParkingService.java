package com.dynacore.livemap.parking;

import com.dynacore.livemap.common.model.FeatureCollection;
import com.dynacore.livemap.common.repo.JpaRepository;
import com.dynacore.livemap.common.service.GeoJsonRequester;
import com.dynacore.livemap.common.tools.HttpGeoJsonSerializer;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Profile("parking")
@Service("parkingPlaceService")
public class ParkingService implements GeoJsonRequester<FeatureCollection<ParkingModel>> {

    private JpaRepository<ParkingEntity> parkingRepo;
    private FeatureCollection<ParkingModel> lastUpdate;
    private ParkingConfiguration config;

    public ParkingService(JpaRepository<ParkingEntity> parkingRepo, ParkingConfiguration config) {
        this.parkingRepo = parkingRepo;
        this.config = config;
        pollRequest();
    }

    private void pollRequest() {
        ScheduledExecutorService exec = Executors.newSingleThreadScheduledExecutor();
        exec.scheduleAtFixedRate(() -> {
            HttpGeoJsonSerializer<FeatureCollection<ParkingModel>> httpGeoJsonSerializer = new HttpGeoJsonSerializer<>();
            try {
                synchronized (this) {
                    lastUpdate = httpGeoJsonSerializer.marshallFromUrl(config.getUrl(), ParkingModel.class);
                    lastUpdate.setTimeOfRetrievalNow();
                }
                saveCollection((lastUpdate));
            } catch (ResponseStatusException responseException) {
                lastUpdate.setErrorReport(responseException.getReason());
            }
        }, config.getInitialDelay(), config.getRequestInterval(), TimeUnit.SECONDS);
    }

    @Transactional
    public synchronized void saveCollection(FeatureCollection<ParkingModel> features) {
        features.getFeatures().forEach(parking -> parkingRepo.save(new ParkingEntity(parking.getId(),
                parking.getName(),
                parking.getPubDate(),
                parking.getProperties().getRetrievedFromThirdParty(),
                parking.getState(),
                parking.getFreeSpaceShort(),
                parking.getFreeSpaceLong(),
                parking.getShortCapacity(),
                parking.getLongCapacity()))
        );
    }

    @Override
    public synchronized FeatureCollection<ParkingModel> getLastUpdate() {
        if (lastUpdate != null) {
            lastUpdate.getFeatures().forEach(ParkingService::setPercentage);
        } else {
            lastUpdate = new FeatureCollection<>();
            lastUpdate.setErrorReport("Error: Could not get data from " + config.getUrl());
        }

        return lastUpdate;
    }

    private static void setPercentage(ParkingModel parking) {
        try {
            parking.setPercentage(((parking.getShortCapacity() - parking.getFreeSpaceShort()) * 100) / parking.getShortCapacity());
        } catch (ArithmeticException divideByZero) {
            parking.setPercentage(-1);
        }
    }
}
