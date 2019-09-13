package com.dynacore.livemap.guidancesign;

import com.dynacore.livemap.common.model.FeatureCollection;
import com.dynacore.livemap.common.repo.JpaRepository;
import com.dynacore.livemap.common.service.GeoJsonRequester;
import com.dynacore.livemap.common.tools.HttpGeoJsonSerializer;
import com.dynacore.livemap.guidancesign.model.GuidanceSign;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service("guidanceSignService")
public class GuidanceSignService implements GeoJsonRequester<FeatureCollection<GuidanceSign>> {

    private final JpaRepository<GuidanceSignLogData> guidanceSignRepository;
    private FeatureCollection<GuidanceSign> featureCollection;
    private GuidanceSignConfiguration config;

    public GuidanceSignService(GuidanceSignRepo guidanceSignRepository, GuidanceSignConfiguration config) {
        this.guidanceSignRepository = guidanceSignRepository;
        this.config = config;
        ScheduledExecutorService exec = Executors.newSingleThreadScheduledExecutor();
        exec.scheduleAtFixedRate(() -> {
            try {
                HttpGeoJsonSerializer<FeatureCollection<GuidanceSign>> httpGeoJsonSerializer = new HttpGeoJsonSerializer<>();
                featureCollection = httpGeoJsonSerializer.marshallFromUrl(config.getUrl(), GuidanceSign.class);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, config.getInitialDelay(), config.getRequestInterval(), TimeUnit.SECONDS);
    }

    public FeatureCollection<GuidanceSign> getLiveData() {
        return featureCollection;
    }

    @Override
    @Transactional
    public void saveCollection(FeatureCollection<GuidanceSign> fc) {

    }
}

