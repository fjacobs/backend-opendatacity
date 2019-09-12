package com.dynacore.livemap.common.service;

/**
 * @param <U> Processed Endpoint JSON representation
 */

public interface TrafficObject<U> {

	U getLiveData();
	
	void saveCollection(U processedCollection);

}


