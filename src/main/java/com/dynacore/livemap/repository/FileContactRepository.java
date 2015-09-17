package com.dynacore.livemap.repository;

import com.dynacore.livemap.entity.hibernate.FileContactLogData;

public interface FileContactRepository {
	FileContactLogData save(FileContactLogData fileContactLogData);
}