package com.dynacore.livemap.traveltime;

import com.dynacore.livemap.common.repo.JpaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;
import java.util.Optional;

@Profile("traveltime")
@Repository("travelTimeRepository")
public class TravelTimeRepo implements JpaRepository<TravelTimeEntity> {
    private Logger logger = LoggerFactory.getLogger(TravelTimeRepo.class);

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public void save(TravelTimeEntity travelTimeEntity) {
        logger.info("SAVE VALLED");
        try {
            entityManager.persist(travelTimeEntity);
            entityManager.flush();
        } catch (Exception error) {
            logger.error(error.toString());
        }
    }

    @Override
    public Optional<TravelTimeEntity> get(long id) {
        return Optional.ofNullable(entityManager.find(TravelTimeEntity.class, id));
    }

    @Override
    public List<TravelTimeEntity> getAll() {
        Query query = entityManager.createQuery("");
        return  query.getResultList();
    }

    @Override
    public void update(TravelTimeEntity travelTimeEntity, String[] params) {
    }

    @Override
    public void delete(TravelTimeEntity travelTimeEntity) {

    }
}
