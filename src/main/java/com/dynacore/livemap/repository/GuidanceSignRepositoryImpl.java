package com.dynacore.livemap.repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.dynacore.livemap.entity.hibernate.GuidanceSignLogData;

@Repository("guidanceSignRepository")
public class GuidanceSignRepositoryImpl implements GuidanceSignRepository {

	@PersistenceContext
	private EntityManager em;

	@Override
	@Transactional
	public GuidanceSignLogData save(GuidanceSignLogData guidanceSign) {
		em.persist(guidanceSign);
		em.flush();
		return guidanceSign;
	}

}
