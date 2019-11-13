package com.dynacore.livemap.traveltime.repo;

import com.dynacore.livemap.traveltime.repo.TravelTimeEntity;
import org.geojson.Feature;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Profile("traveltime")
@Repository
public interface RoadReactRepository extends ReactiveCrudRepository<TravelTimeEntity, Long> {
//
//    @Query("SELECT * FROM person WHERE lastname = :lastname")
//    Flux<Feature> findByLastname(String lastname);

    @Query("SELECT pub_date, id FROM travel_time WHERE id = $1")
    Flux<Feature> findEntity(String id);
}
