package com.dynacore.livemap.config;

import com.dynacore.livemap.repository.GuidanceSignRepositoryImpl;
import com.dynacore.livemap.repository.JpaRepository;
import com.dynacore.livemap.repository.ParkingPlaceRepositoryImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    @Bean
    public JpaRepository parkingRepo() {
        return new ParkingPlaceRepositoryImpl();
    }

    @Bean
    public JpaRepository guidanceSignRepo() {
        return new GuidanceSignRepositoryImpl();
    }
}
