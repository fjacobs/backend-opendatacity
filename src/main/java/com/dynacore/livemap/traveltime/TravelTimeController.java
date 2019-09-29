package com.dynacore.livemap.traveltime;

import org.geojson.FeatureCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import reactor.core.publisher.DirectProcessor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxProcessor;
import reactor.core.publisher.FluxSink;

import java.util.concurrent.atomic.AtomicLong;

@Profile("traveltime")
@Controller
public class TravelTimeController {

    final FluxProcessor directProcessor;
    final FluxSink sink;
    final AtomicLong counter;
    private final Logger logger = LoggerFactory.getLogger(TravelTimeController.class);
    private TravelTimeService travelTimeService;
    private TravelTimeConfiguration config;
    @Autowired
    public TravelTimeController(TravelTimeService travelTimeService) {
        this.travelTimeService = travelTimeService;
        this.directProcessor = DirectProcessor.create().serialize();
        this.sink = directProcessor.sink();
        this.counter = new AtomicLong();
    }

    @CrossOrigin(origins = "http://localhost:63342")
    @GetMapping("/subscribett")
    public Flux featureUpdateTest() {
        return directProcessor.map(e -> ServerSentEvent.builder(e)
                .event("featureUpdate")
                .build());
    }

//    @CrossOrigin(origins = "http://localhost:63342")
//    @GetMapping("/fc")
//    public Flux<ServerSentEvent<Feature>> streamEvents() {
//        return travelTimeService.getFluxFromMonoGeoJson(config.getUrl())
//                .map(feature -> ServerSentEvent.<Feature>builder()
//                        .id("road stream")
//                        .event("roadEvent")
//                        .build());
//    }



    @GetMapping("/xxx")
    public void  xx() {

    }



    @CrossOrigin(origins = "http://localhost:63342")
    @GetMapping(value = "getfeaturecollection")
    @ResponseBody
    public FeatureCollection getTravelTimeFc() {
        FeatureCollection fc =  travelTimeService.getFc();
        return fc;
    }



//------------------------------ werkt (PROCESSOR)


//    @GetMapping("/send")
//    public void test() {
//        sink.next("Hello World #" + counter.getAndIncrement());
//    }
//
//    @CrossOrigin(origins = "http://localhost:63342")
//    @GetMapping("/subscribett")
//    public Flux<ServerSentEvent> featureUpdate() {
//        return directProcessor.map(e -> ServerSentEvent.builder(e)
//                .event("featureUpdate")
//                .build());
//    }
//

}
