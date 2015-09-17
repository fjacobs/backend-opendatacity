package com.dynacore.livemap.repository;

import com.dynacore.livemap.entity.hibernate.BrugContactLogData;

public interface BrugContactRepository {
	BrugContactLogData save(BrugContactLogData parkingLogData);
}