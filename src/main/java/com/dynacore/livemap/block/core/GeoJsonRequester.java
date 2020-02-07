package com.dynacore.livemap.block.core;

import com.dynacore.livemap.block.core.model.FeatureBlock;
import com.dynacore.livemap.block.core.model.FeatureCollectionBlock;

/** @param <T> Processed Endpoint JSON representation */
public interface GeoJsonRequester<T extends FeatureCollectionBlock<? extends FeatureBlock>> {

  T getLastUpdate();

  void saveCollection(T processedCollection);
}
