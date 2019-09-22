package com.dynacore.livemap.traveltime;

import com.dynacore.livemap.common.repo.JpaRepository;
import org.geojson.FeatureCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


@Service("travelTimeService")
public class TravelTimeService {

    FeatureCollection lastUpdate;
    private JpaRepository<TravelTimeEntity> travelTimeRepo;
    private TravelTimeConfiguration config;
    private Logger logger = LoggerFactory.getLogger(TravelTimeService.class);

    public TravelTimeService(JpaRepository<TravelTimeEntity> travelTimeRepo, TravelTimeConfiguration config) {
        this.travelTimeRepo = travelTimeRepo;
        this.config = config;
        pollRequest();
    }

    private void pollRequest() {
        ScheduledExecutorService exec = Executors.newSingleThreadScheduledExecutor();
        RestTemplate restTemplate = createRestClient();

        exec.scheduleAtFixedRate(() -> {
            ResponseEntity<FeatureCollection> response
                    = restTemplate.getForEntity(config.getUrl(), FeatureCollection.class);
            lastUpdate = response.getBody();
            processCollection(lastUpdate);
            saveCollection(lastUpdate);
        }, config.getInitialDelay(), config.getRequestInterval(), TimeUnit.SECONDS);
    }

    private void processCollection(FeatureCollection travelTime) {
        int travelTimeVal;
        LocalDateTime timeStamp = LocalDateTime.now();
        lastUpdate.getFeatures().stream().forEach(
                feature -> {
                    feature.getProperties().put("retrievedFromThirdParty", timeStamp);

                    if (!feature.getProperties().containsKey("Traveltime")) {
                        feature.getProperties().put("Traveltime", -1);
                    }
                    if (!feature.getProperties().containsKey("Velocity")) {
                        feature.getProperties().put("Velocity", -1);
                    }
                    if (!feature.getProperties().containsKey("Length")) {
                        feature.getProperties().put("Length", -1);
                    }
                }
        );
    }


    @Transactional
    public void saveCollection(FeatureCollection travelTimeFc) {
        logger.info("saveCollection VALLED");
        try {
            travelTimeFc.getFeatures().forEach(travelTime ->
                        travelTimeRepo.save(
                                new TravelTimeEntity.Builder()
                                        .id((String) travelTime.getProperties().get("Id"))
                                        .name((String) travelTime.getProperties().get("Name"))
                                        .pubDate((String) travelTime.getProperties().get("Timestamp"))
                                        .retrievedFromThirdParty((LocalDateTime) travelTime.getProperties().get("retrievedFromThirdParty"))
                                        .type((String) travelTime.getProperties().get("Type"))
                                        .travelTime((int) travelTime.getProperties().get("Traveltime"))
                                        .velocity((int) travelTime.getProperties().get("Velocity"))
                                        .length((int) travelTime.getProperties().get("Length"))
                                        .build()));
        } catch (Exception error) {
            error.printStackTrace();
        }
    }


    FeatureCollection getLastUpdate() {
        return lastUpdate;
    }

    private RestTemplate createRestClient() {
        RestTemplate restTemplate = new RestTemplate();

        List<MediaType> supportedMediaTypes = new ArrayList<>();
        supportedMediaTypes.add(MediaType.ALL);

        MappingJackson2HttpMessageConverter jacksonMessageConverter = new MappingJackson2HttpMessageConverter();
        jacksonMessageConverter.setSupportedMediaTypes(supportedMediaTypes);

        restTemplate.getMessageConverters().add(jacksonMessageConverter);
        return restTemplate;
    }


}
