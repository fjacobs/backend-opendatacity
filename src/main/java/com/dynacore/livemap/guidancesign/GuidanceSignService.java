package com.dynacore.livemap.guidancesign;

import com.dynacore.livemap.common.model.FeatureCollection;
import com.dynacore.livemap.common.repo.JpaRepository;
import com.dynacore.livemap.common.service.TrafficObject;
import com.dynacore.livemap.guidancesign.model.GuidanceSign;
import com.dynacore.livemap.tools.HttpGeoJsonSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service("guidanceSignService")
public class GuidanceSignService implements TrafficObject<FeatureCollection<GuidanceSign>> {

    private static final int POLLING_INITIAL_DELAY = 0;
    private static final int POLLING_UPDATE_INTERVAL = 60;
    private final JpaRepository<GuidanceSignLogData> guidanceSignRepository;
    private FeatureCollection<GuidanceSign> featureCollection;
    private String DATA_SOURCE_URL_KEY = "http://opd.it-t.nl/Data/parkingdata/v1/amsterdam/GuidanceSign.json";

    @Autowired
    public GuidanceSignService(GuidanceSignRepo guidanceSignRepository) {
        this.guidanceSignRepository = guidanceSignRepository;

        ScheduledExecutorService exec = Executors.newSingleThreadScheduledExecutor();
        exec.scheduleAtFixedRate(() -> {
            try {
                HttpGeoJsonSerializer<FeatureCollection<GuidanceSign>> httpGeoJsonSerializer = new HttpGeoJsonSerializer<>();
                featureCollection = httpGeoJsonSerializer.marshallFromUrl(DATA_SOURCE_URL_KEY, GuidanceSign.class);
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

