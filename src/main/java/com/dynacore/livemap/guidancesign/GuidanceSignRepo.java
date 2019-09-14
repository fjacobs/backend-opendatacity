package com.dynacore.livemap.guidancesign;

import com.dynacore.livemap.common.repo.JpaRepository;
import com.dynacore.livemap.guidancesign.dto.GuidanceSignDTO;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;
import java.util.Optional;

@Repository("guidanceSignRepository")
public class GuidanceSignRepo implements JpaRepository<GuidanceSignDTO> {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public void save(GuidanceSignDTO guidanceSignDTO) {

        EntityTransaction tx = null;
        Session session = null;

        entityManager.persist(guidanceSignDTO);
        guidanceSignDTO.getInnerDisplays().stream().forEach(display -> entityManager.persist(display));
        entityManager.flush();
    }

    @Override
    public Optional<GuidanceSignDTO> get(long id) {
        return Optional.ofNullable(entityManager.find(GuidanceSignDTO.class, 12));
    }

    @Override
    public List<GuidanceSignDTO> getAll() {
        Query query = entityManager.createQuery("");
        return query.getResultList();
    }

    @Override
    public void update(GuidanceSignDTO guidanceSignDTOLogData, String[] params) {
    }

    @Override
    public void delete(GuidanceSignDTO guidanceSignDTOLogData) {
    }
}
