package com.dynacore.livemap.guidancesign;

import com.dynacore.livemap.common.repo.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;
import java.util.Optional;

@Repository("guidanceignrepository")
public class GuidanceSignRepo implements JpaRepository<GuidanceSignLogData> {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public void save(GuidanceSignLogData guidanceSign) {
        entityManager.persist(guidanceSign);
        entityManager.flush();
    }

    @Override
    public Optional<GuidanceSignLogData> get(long id) {
        return Optional.ofNullable(entityManager.find(GuidanceSignLogData.class, id));
    }

    @Override
    public List<GuidanceSignLogData> getAll() {
        Query query = entityManager.createQuery("");
        return query.getResultList();
    }

    @Override
    public void update(GuidanceSignLogData guidanceSignLogData, String[] params) {
    }

    @Override
    public void delete(GuidanceSignLogData guidanceSignLogData) {
    }
}
