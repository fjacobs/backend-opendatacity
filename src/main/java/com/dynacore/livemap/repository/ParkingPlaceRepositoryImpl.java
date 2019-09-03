package com.dynacore.livemap.repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.dynacore.livemap.entity.hibernate.ParkingLogData;

import java.util.List;
import java.util.Optional;

@Repository("parkingPlaceRepository")
public class ParkingPlaceRepositoryImpl implements JpaRepository<ParkingLogData> {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public void save(ParkingLogData ParkingLogData) {
        entityManager.persist(ParkingLogData);
        entityManager.flush();
    }

    @Override
    public Optional<ParkingLogData> get(long id) {
        return Optional.ofNullable(entityManager.find(ParkingLogData.class, id));
    }

    @Override
    public List<ParkingLogData> getAll() {
        Query query = entityManager.createQuery("SELECT e FROM ParkingLogData e");
        return query.getResultList();
    }

    @Override
    public void update(ParkingLogData ParkingLogData, String[] params) {
    }

    @Override
    public void delete(ParkingLogData ParkingLogData) {

    }
}
