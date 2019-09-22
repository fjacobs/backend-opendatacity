package com.dynacore.livemap.traveltime;

import com.dynacore.livemap.common.model.FeatureCollection;
import com.dynacore.livemap.common.repo.JpaRepository;
import com.dynacore.livemap.common.service.GeoJsonRequester;
import com.dynacore.livemap.parking.ParkingConfiguration;
import com.dynacore.livemap.parking.ParkingModel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service("travelTimeService")
public class TravelTimeService implements GeoJsonRequester<FeatureCollection<TravelTimeModel>> {

    private JpaRepository<TravelTimeEntity> travelTimeRepo;
    private FeatureCollection<ParkingModel> getLastUpdate;
    private ParkingConfiguration config;

    public TravelTimeService(JpaRepository<TravelTimeEntity> travelTimeRepo, TravelTimeConfiguration config) {
    }

    private void pollRequest() {
    }


    @Transactional
    public void saveCollection(FeatureCollection<TravelTimeModel> features) {
    }

    @Override
    public FeatureCollection<TravelTimeModel> getLastUpdate() {
        return null;
    }
}
