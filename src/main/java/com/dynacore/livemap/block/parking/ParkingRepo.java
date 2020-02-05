package com.dynacore.livemap.block.parking;

import com.dynacore.livemap.block.core.repo.JpaRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;
import java.util.Optional;

@Profile("parking")
@Repository("parkingPlaceRepository")
public class ParkingRepo implements JpaRepository<ParkingEntity> {

  @PersistenceContext private EntityManager entityManager;

  @Override
  @Transactional
  public void save(ParkingEntity parkingEntity) {
    entityManager.persist(parkingEntity);
    entityManager.flush();
  }

  @Override
  public Optional<ParkingEntity> get(long id) {
    return Optional.ofNullable(entityManager.find(ParkingEntity.class, id));
  }

  @Override
  public List<ParkingEntity> getAll() {
    Query query = entityManager.createQuery("");
    return query.getResultList();
  }

  @Override
  public void update(ParkingEntity parkingEntity, String[] params) {}

  @Override
  public void delete(ParkingEntity parkingEntity) {}
}
