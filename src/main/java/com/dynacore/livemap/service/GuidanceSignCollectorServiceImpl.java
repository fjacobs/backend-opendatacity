package com.dynacore.livemap.service;

import com.dynacore.livemap.entity.hibernate.GuidanceSignLogData;
import com.dynacore.livemap.entity.jsonrepresentations.FeatureCollection;
import com.dynacore.livemap.entity.jsonrepresentations.guidancesign.GuidanceSign;
import com.dynacore.livemap.repository.JpaRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service("guidanceSignService")
public class GuidanceSignCollectorServiceImpl implements TrafficDataCollectorService<FeatureCollection<GuidanceSign>> {

    private final JpaRepository<GuidanceSignLogData> guidanceSignRepository;
    private FeatureCollection<GuidanceSign> featureCollection;

    private static final int POLLING_INITIAL_DELAY = 0;
    private static final int POLLING_UPDATE_INTERVAL = 60;

    private String DATA_SOURCE_URL_KEY =  "http://opd.it-t.nl/Data/parkingdata/v1/amsterdam/GuidanceSign.json";

    @Autowired
    public GuidanceSignCollectorServiceImpl(JpaRepository<GuidanceSignLogData> guidanceSignRepository) {
        this.guidanceSignRepository = guidanceSignRepository;

        ScheduledExecutorService exec = Executors.newSingleThreadScheduledExecutor();
        exec.scheduleAtFixedRate(() -> {
            try {
                RestMapper<FeatureCollection<GuidanceSign>> restMapper = new RestMapper<>();
                featureCollection = restMapper.marshallFromUrl(DATA_SOURCE_URL_KEY, GuidanceSign.class);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, POLLING_INITIAL_DELAY, POLLING_UPDATE_INTERVAL, TimeUnit.SECONDS);
    }

    public FeatureCollection<GuidanceSign> getLiveData() {
        return featureCollection;
    }

    @Override
    @Transactional
    public void saveCollection(FeatureCollection<GuidanceSign> fc) {

    }
}



