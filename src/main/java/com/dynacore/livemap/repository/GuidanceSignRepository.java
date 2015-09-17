package com.dynacore.livemap.repository;

import com.dynacore.livemap.entity.hibernate.GuidanceSignLogData;

public interface GuidanceSignRepository {
	GuidanceSignLogData save(GuidanceSignLogData parkingLogData);
}