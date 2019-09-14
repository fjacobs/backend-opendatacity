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
public class ParkingRepo implements JpaRepository<ParkingDTO> {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public void save(ParkingDTO parkingDTO) {
        entityManager.persist(parkingDTO);
        entityManager.flush();
    }

    @Override
    public Optional<ParkingDTO> get(long id) {
        return Optional.ofNullable(entityManager.find(ParkingDTO.class, id));
    }

    @Override
    public List<ParkingDTO> getAll() {
        Query query = entityManager.createQuery("");
        return query.getResultList();
    }

    @Override
    public void update(ParkingDTO ParkingDTO, String[] params) {
    }

    @Override
    public void delete(ParkingDTO ParkingDTO) {

    }
}
