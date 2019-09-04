package com.dynacore.livemap.service;

import com.dynacore.livemap.entity.hibernate.GuidanceSignLogData;
import com.dynacore.livemap.entity.jsonrepresentations.GeoJsonCollection;
import com.dynacore.livemap.entity.jsonrepresentations.guidancesign.GuidanceSign;
import com.dynacore.livemap.repository.JpaRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service("guidanceSignService")
public class GuidanceSignCollectorServiceImpl implements TrafficDataCollectorService<GeoJsonCollection<GuidanceSign>> {

    @Autowired
    private JpaRepository<GuidanceSignLogData> guidanceSignRepository;
    private GeoJsonCollection<GuidanceSign> geoJsonCollection;

    private static final int POLLING_INITIAL_DELAY = 0;
    private static final int POLLING_UPDATE_INTERVAL = 60;

    @Value("${vialis.amsterdam.guidancesign.jsonurl}")
    private String DATA_SOURCE_URL_KEY;

    public GuidanceSignCollectorServiceImpl() {
        RestMapper<GeoJsonCollection<GuidanceSign>> restMapper = new RestMapper();

        ScheduledExecutorService exec = Executors.newSingleThreadScheduledExecutor();
        exec.scheduleAtFixedRate(() -> {
            try {
                geoJsonCollection = restMapper.marshallFromUrl(DATA_SOURCE_URL_KEY, GuidanceSign.class);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, POLLING_INITIAL_DELAY, POLLING_UPDATE_INTERVAL, TimeUnit.SECONDS);
    }

    public GeoJsonCollection<GuidanceSign> getLiveData() {
        return geoJsonCollection;
    }

    @Override
    @Transactional
    public void saveCollection(GeoJsonCollection<GuidanceSign> fc) {
        for (int i = 0; i < fc.getFeatures().size(); i++) {
            GuidanceSignLogData property = new GuidanceSignLogData(
                    fc.getFeatures().get(i).getName(),
                    fc.getFeatures().get(i).getPubDate(),
                    fc.getFeatures().get(i).getState()
            );
            guidanceSignRepository.save(property);
        }
    }
}



