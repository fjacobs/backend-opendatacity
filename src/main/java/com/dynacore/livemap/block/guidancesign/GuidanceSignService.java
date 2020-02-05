package com.dynacore.livemap.block.guidancesign;

import com.dynacore.livemap.block.core.model.FeatureCollectionBlock;
import com.dynacore.livemap.block.core.repo.JpaRepository;
import com.dynacore.livemap.core.service.GeoJsonRequester;
import com.dynacore.livemap.block.core.HttpGeoJsonSerializer;
import com.dynacore.livemap.block.guidancesign.entity.GuidanceSignEntity;
import com.dynacore.livemap.block.guidancesign.model.GuidanceSignModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Profile("guidancesign")
@Service("guidanceSignService")
public class GuidanceSignService implements GeoJsonRequester<FeatureCollectionBlock<GuidanceSignModel>> {

  private JpaRepository<GuidanceSignEntity> guidanceSignRepository;
  private FeatureCollectionBlock<GuidanceSignModel> featureCollectionBlock;
  private GuidanceSignConfiguration config;

  @Autowired
  public GuidanceSignService(
      GuidanceSignRepo guidanceSignRepository, GuidanceSignConfiguration config) {
    this.guidanceSignRepository = guidanceSignRepository;
    this.config = config;
    ScheduledExecutorService exec = Executors.newSingleThreadScheduledExecutor();
    exec.scheduleAtFixedRate(
        () -> {
          HttpGeoJsonSerializer<FeatureCollectionBlock<GuidanceSignModel>> httpGeoJsonSerializer =
              new HttpGeoJsonSerializer<>();
          synchronized (this) {
            featureCollectionBlock =
                httpGeoJsonSerializer.marshallFromUrl(config.getUrl(), GuidanceSignModel.class);
            featureCollectionBlock.setTimeOfRetrievalNow();
          }
          saveCollection(featureCollectionBlock);
        },
        config.getInitialDelay().getSeconds(),
        config.getRequestInterval().getSeconds(),
        TimeUnit.SECONDS);
  }

  public synchronized FeatureCollectionBlock<GuidanceSignModel> getLastUpdate() {
    return featureCollectionBlock;
  }

  @Override
  public synchronized void saveCollection(FeatureCollectionBlock<GuidanceSignModel> featureColl) {
    featureColl.getFeatures().stream()
        .map(GuidanceSignEntity::new)
        .forEach(guidanceSignDTO -> guidanceSignRepository.save(guidanceSignDTO));
  }
}
