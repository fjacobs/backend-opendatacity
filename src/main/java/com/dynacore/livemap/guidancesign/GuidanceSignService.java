package com.dynacore.livemap.guidancesign;

import com.dynacore.livemap.common.model.FeatureCollection;
import com.dynacore.livemap.common.repo.JpaRepository;
import com.dynacore.livemap.common.service.GeoJsonRequester;
import com.dynacore.livemap.common.tools.HttpGeoJsonSerializer;
import com.dynacore.livemap.guidancesign.dto.GuidanceSignDTO;
import com.dynacore.livemap.guidancesign.dto.InnerDisplayDTO;
import com.dynacore.livemap.guidancesign.model.GuidanceSignModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service("guidanceSignService")
public class GuidanceSignService implements GeoJsonRequester<FeatureCollection<GuidanceSignModel>> {

    private JpaRepository<GuidanceSignDTO> guidanceSignRepository;
    private FeatureCollection<GuidanceSignModel> featureCollection;
    private GuidanceSignConfiguration config;

    @Autowired
    public GuidanceSignService(GuidanceSignRepo guidanceSignRepository, GuidanceSignConfiguration config) {
        this.guidanceSignRepository = guidanceSignRepository;
        this.config = config;
        ScheduledExecutorService exec = Executors.newSingleThreadScheduledExecutor();
        exec.scheduleAtFixedRate(() -> {
            try {
                HttpGeoJsonSerializer<FeatureCollection<GuidanceSignModel>> httpGeoJsonSerializer = new HttpGeoJsonSerializer<>();
                featureCollection = httpGeoJsonSerializer.marshallFromUrl(config.getUrl(), GuidanceSignModel.class);
                saveCollection(featureCollection);
            } catch (Exception e) {
                throw new RuntimeException("Can't serialize: " + config.getUrl());
            }
        }, config.getInitialDelay(), config.getRequestInterval(), TimeUnit.SECONDS);
    }

    public FeatureCollection<GuidanceSignModel> getLiveData() {
        return featureCollection;
    }

    @Override
    public void saveCollection(FeatureCollection<GuidanceSignModel> processedCollection) {
        GuidanceSignDTO guidanceSign = new GuidanceSignDTO.Builder()
                .Name("name")
                .PubDate("sdfsdsdf")
                .State("sdfsdfsf")
                .build();

        InnerDisplayDTO innerDisplay1 = new InnerDisplayDTO.Builder()
                .OutputDescription("Marge")
                .Output("Springfield")
                .Type("type")
                .Description("desc")
                .guidanceSignDTO(guidanceSign)
                .build();

        Set set = new HashSet();
        set.add(innerDisplay1);

        InnerDisplayDTO innerDisplay2 = new InnerDisplayDTO.Builder()
                .OutputDescription("sadaMarge")
                .Output("Springfiasdaeld")
                .Type("tyasdpe")
                .Description("deasdsc")
                .guidanceSignDTO(guidanceSign)
                .build();

        set.add(innerDisplay2);

        guidanceSign.setInnerDisplays(set);
        guidanceSignRepository.save(guidanceSign);
    }
}

