package com.dynacore.livemap.guidancesign.repo;

import com.dynacore.livemap.core.PubDateSizeResponse;
import com.dynacore.livemap.core.repository.TrafficRepository;
import com.dynacore.livemap.guidancesign.domain.GuidanceSignEntity;
import com.dynacore.livemap.guidancesign.domain.InnerDisplayEntity;
import com.dynacore.livemap.traveltime.repo.TravelTimeRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.data.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.OffsetDateTime;

@Profile("guidancesign")
@Repository("guidanceSignRepository")
public class GuidanceSignRepo implements TrafficRepository<GuidanceSignEntity> {

  private DatabaseClient db;
  private static final Logger log = LoggerFactory.getLogger(TravelTimeRepo.class);

  public GuidanceSignRepo(DatabaseClient databaseClient) {
    this.db = databaseClient;
  }

  @Transactional
  public Mono<Void> save(GuidanceSignEntity entity) {

    //         Nested is not supported yet and can't be supressed with @Transient:
    //            return db.insert()
    //                     .into(GuidanceSignEntity.class)
    //                     .using(entity).fetch().rowsUpdated()
    //                     .then(db.insert().into(InnerDisplayEntity.class).using(entity.getInnerDisplays()).fetch().rowsUpdated()).then();
    return db.execute(
            "insert into guidance_sign ( id, name, pub_date, retrieved_from_third_party, removed, state)  VALUES(:id,:name,:pub_date,:retrieved_from_third_party,:removed,:state)")
        .bind("id", entity.getId())
        .bind("name", entity.getName())
        .bind("pub_date", entity.getPubDate())
        .bind("retrieved_from_third_party", entity.getOurCreationDate())
        .bind("removed", entity.getRemoved())
        .bind("state", entity.getState())
        .then()
        .then(
            db.insert()
                .into(InnerDisplayEntity.class)
                .using(entity.getInnerDisplays())
                .fetch()
                .rowsUpdated())
        .then();
  }

  @Override
  public Mono<Boolean> isNew(GuidanceSignEntity entity) {
    return null;
  }

  @Override
  public Mono<GuidanceSignEntity> getLatest(String entityId) {
    return null;
  }

  @Override
  public Flux<GuidanceSignEntity> getAllAscending() {
    return null;
  }

  @Override
  public Flux<PubDateSizeResponse> getReplayMetaData() {
    return null;
  }

  @Override
  public Flux<PubDateSizeResponse> getReplayMetaData(OffsetDateTime start, OffsetDateTime end) {
    return null;
  }

  @Override
  public Flux<GuidanceSignEntity> getFeatureDateRange(OffsetDateTime start, OffsetDateTime end) {
    return null;
  }
}

// class TransactionalService {
//
//    final DatabaseClient db
//
//    TransactionalService(DatabaseClient db) {
//        this.db = db;
//    }
//
//    @Transactional
//    Mono<Void> insertRows() {
//        return db.execute("INSERT INTO person (name, age) VALUES('Joe', 34)")
//                .fetch().rowsUpdated()
//                .then(db.execute("INSERT INTO contacts (name) VALUES('Joe')")
//                        .then());
//    }
// }
