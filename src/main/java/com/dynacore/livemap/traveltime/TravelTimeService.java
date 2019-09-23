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

    private static final String ID = "Id";
    private static final String NAME = "Name";
    private static final String TYPE = "Type";
    private static final String TRAVEL_TIME = "Traveltime";
    private static final String LENGTH = "Length";
    private static final String VELOCITY = "Velocity";
    private static final String OUR_RETRIEVAL = "retrievedFromThirdParty";
    private static final String THEIR_RETRIEVAL = "Timestamp";
    private static final String DYNACORE_ERRORS = "dynacoreErrors";

    private FeatureCollection lastUpdate;
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
            ResponseEntity<FeatureCollection> response = restTemplate.getForEntity(config.getUrl(), FeatureCollection.class);
            synchronized(this) {
                lastUpdate = response.getBody();
            }
            processCollection(lastUpdate);
            saveCollection(lastUpdate);

        }, config.getInitialDelay(), config.getRequestInterval(), TimeUnit.SECONDS);
    }

    private synchronized void processCollection(FeatureCollection travelTimeFc) {
        LocalDateTime retrieved = LocalDateTime.now();
        travelTimeFc.getFeatures().forEach(
                feature -> {
                        feature.getProperties().put(DYNACORE_ERRORS, "none");
                        feature.getProperties().put(OUR_RETRIEVAL, retrieved);
                        if (!feature.getProperties().containsKey(TRAVEL_TIME)) {
                            feature.getProperties().put(TRAVEL_TIME, -1);
                        }
                        if (!feature.getProperties().containsKey(VELOCITY)) {
                            feature.getProperties().put(VELOCITY, -1);
                        }
                        if (!feature.getProperties().containsKey(LENGTH)) {
                            feature.getProperties().put(LENGTH, -1);
                        }
                }
        );
    }


    @Transactional
    public synchronized void saveCollection(FeatureCollection travelTimeFc) {
        try {
            travelTimeFc.getFeatures().forEach(travelTime -> travelTimeRepo.save(
                    new TravelTimeEntity.Builder()
                            .id((String) travelTime.getProperties().get(ID))
                            .name((String) travelTime.getProperties().get(NAME))
                            .pubDate((String) travelTime.getProperties().get(THEIR_RETRIEVAL))
                            .retrievedFromThirdParty((LocalDateTime) travelTime.getProperties().get(OUR_RETRIEVAL))
                            .type((String) travelTime.getProperties().get(TYPE))
                            .travelTime((int) travelTime.getProperties().get(TRAVEL_TIME))
                            .velocity((int) travelTime.getProperties().get(VELOCITY))
                            .length((int) travelTime.getProperties().get(LENGTH))
                            .build()));
        } catch (Exception error) {
            logger.error("Can't save road information to DB: " + error.toString());
        }
    }


    synchronized FeatureCollection getLastUpdate() {
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
