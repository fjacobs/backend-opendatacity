package com.dynacore.livemap.repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.dynacore.livemap.entity.hibernate.BrugContactLogData;

@Repository("brugContactRepository")
public class BrugContactRepositoryImpl implements BrugContactRepository {

	@PersistenceContext
	private EntityManager em;

	@Override
	@Transactional
	public BrugContactLogData save(BrugContactLogData brugContact) {
		em.persist(brugContact);
		em.flush();
		return brugContact;
	}

}
