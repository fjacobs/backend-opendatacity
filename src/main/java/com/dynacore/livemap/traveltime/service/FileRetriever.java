package com.dynacore.livemap.traveltime.service;

import com.dynacore.livemap.core.tools.FileToGeojson;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

@Configuration
public class FileRetriever {

    @Profile("file")
    @Bean(name="getThreeRepeat")
    OpenDataRetriever getThreeFullRepeat() {
        return (interval) -> Flux.fromIterable(FileToGeojson.readCollection("/traveltimedata/real/"))
                                 .publishOn(Schedulers.boundedElastic())
                                 .delayElements(interval)
                                 .repeat();
    }
}
