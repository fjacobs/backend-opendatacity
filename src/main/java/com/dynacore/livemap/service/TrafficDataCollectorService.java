package com.dynacore.livemap.service;

/**
 * @param <U> Processed Endpoint JSON representation
 */

public interface TrafficDataCollectorService<U> {

	U getLiveData();
	
	void saveCollection(U processedCollection);

}


