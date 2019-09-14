package com.dynacore.livemap.parking;

import com.dynacore.livemap.common.repo.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;
import java.util.Optional;

@Repository("parkingPlaceRepository")
public class ParkingRepo implements JpaRepository<ParkingEntity> {

    @PersistenceContext
    private EntityManager entityManager;

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
    public void update(ParkingEntity ParkingEntity, String[] params) {
    }

    @Override
    public void delete(ParkingEntity ParkingEntity) {

    }
}
