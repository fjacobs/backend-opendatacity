package com.dynacore.livemap;

import com.dynacore.livemap.common.repo.JpaRepository;
import com.dynacore.livemap.guidancesign.GuidanceSignRepo;
import com.dynacore.livemap.parking.ParkingRepo;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    @Bean
    public JpaRepository parkingRepo() {
        return new ParkingRepo();
    }

    @Bean
    public JpaRepository guidanceSignRepo() {
        return new GuidanceSignRepo();
    }

//    @Bean
//    public JpaRepository travelTimeRepo() {
//        return new TravelTimeRepo();
//    }
}
