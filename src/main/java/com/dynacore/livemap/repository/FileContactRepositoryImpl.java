package com.dynacore.livemap.repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.dynacore.livemap.entity.hibernate.FileContactLogData;

@Repository("fileContactRepository")
public class FileContactRepositoryImpl implements FileContactRepository {

	@PersistenceContext
	private EntityManager em;

	@Override
	@Transactional
	public FileContactLogData save(FileContactLogData fileContact) {
		em.persist(fileContact);
		em.flush();
		return fileContact;
	}

}
