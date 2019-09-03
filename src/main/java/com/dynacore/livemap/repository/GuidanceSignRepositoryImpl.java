package com.dynacore.livemap.repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.dynacore.livemap.entity.hibernate.GuidanceSignLogData;

import java.util.List;
import java.util.Optional;

@Repository("guidancesignrepository")
public class GuidanceSignRepositoryImpl implements JpaRepository<GuidanceSignLogData> {

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
		Query query = entityManager.createQuery("SELECT e FROM GuidanceSign e");
		return query.getResultList();
	}

	@Override
	public void update(GuidanceSignLogData guidanceSignLogData, String[] params) {

	}

	@Override
	public void delete(GuidanceSignLogData guidanceSignLogData) {

	}
}
