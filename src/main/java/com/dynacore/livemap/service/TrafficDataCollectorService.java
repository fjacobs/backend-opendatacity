package com.dynacore.livemap.service;

/**
 * @param <U> Processed Endpoint JSON representation
 */

public interface TrafficDataCollectorService<U> {
	U getProcessedJson();
	void saveCollection(U processedCollection);
}


