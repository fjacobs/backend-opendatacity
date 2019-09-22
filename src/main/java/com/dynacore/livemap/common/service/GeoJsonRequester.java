package com.dynacore.livemap.common.service;

import com.dynacore.livemap.common.model.Feature;
import com.dynacore.livemap.common.model.FeatureCollection;

/**
 * @param <T> Processed Endpoint JSON representation
 */
public interface GeoJsonRequester<T extends FeatureCollection<? extends Feature>> {

	T getLastUpdate();

	void saveCollection(T processedCollection);

}


