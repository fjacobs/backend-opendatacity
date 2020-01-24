package com.dynacore.livemap.configuration.adapter;

import com.dynacore.livemap.core.service.GeoJsonAdapter;
import com.dynacore.livemap.core.tools.FileToGeojson;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

@Configuration
public class FileAdapter {

    private FileAdapterConfig config;

    public FileAdapter(FileAdapterConfig config) {
        this.config = config;
    }

    @Profile("file")
    @Bean(name="fileReaderRepeat")
    GeoJsonAdapter fileReaderRepeat() {
        return (interval) -> Flux.fromIterable(FileToGeojson.readCollection(config.getFolder()))
                                 .publishOn(Schedulers.boundedElastic())
                                 .delayElements(interval)
                                 .repeat();
    }
}
