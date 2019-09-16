package com.dynacore.livemap.guidancesign;

import com.dynacore.livemap.common.model.FeatureCollection;
import com.dynacore.livemap.common.repo.JpaRepository;
import com.dynacore.livemap.common.service.GeoJsonRequester;
import com.dynacore.livemap.common.tools.HttpGeoJsonSerializer;
import com.dynacore.livemap.guidancesign.entity.GuidanceSignEntity;
import com.dynacore.livemap.guidancesign.model.GuidanceSignModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service("guidanceSignService")
public class GuidanceSignService implements GeoJsonRequester<FeatureCollection<GuidanceSignModel>> {

    private JpaRepository<GuidanceSignEntity> guidanceSignRepository;
    private FeatureCollection<GuidanceSignModel> featureCollection;
    private GuidanceSignConfiguration config;
    Logger logger = LoggerFactory.getLogger(GuidanceSignService.class);

    @Autowired
    public GuidanceSignService(GuidanceSignRepo guidanceSignRepository, GuidanceSignConfiguration config) {
        this.guidanceSignRepository = guidanceSignRepository;
        this.config = config;
        ScheduledExecutorService exec = Executors.newSingleThreadScheduledExecutor();
        exec.scheduleAtFixedRate(() -> {
                HttpGeoJsonSerializer<FeatureCollection<GuidanceSignModel>> httpGeoJsonSerializer = new HttpGeoJsonSerializer<>();
                featureCollection = httpGeoJsonSerializer.marshallFromUrl(config.getUrl(), GuidanceSignModel.class);
                saveCollection(featureCollection);
        }, config.getInitialDelay(), config.getRequestInterval(), TimeUnit.SECONDS);
    }

    public FeatureCollection<GuidanceSignModel> getLiveData() {
        return featureCollection;
    }

    @Override
    public void saveCollection(FeatureCollection<GuidanceSignModel> featureColl) {
        featureColl.getFeatures()
                .stream()
                .map(GuidanceSignEntity::new)
                .forEach(guidanceSignDTO -> guidanceSignRepository.save(guidanceSignDTO));
    }
}

