package com.dynacore.livemap.core.service;

import com.dynacore.livemap.core.model.Feature;
import com.dynacore.livemap.core.model.FeatureCollection;

/** @param <T> Processed Endpoint JSON representation */
public interface GeoJsonRequester<T extends FeatureCollection<? extends Feature>> {

  T getLastUpdate();

  void saveCollection(T processedCollection);
}
