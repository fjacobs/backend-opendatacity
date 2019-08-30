package com.dynacore.livemap.service;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * @param <U> Processed Endpoint JSON representation
 */

public interface TrafficDataCollectorService<U> {
	 static final String dataSourceProperty = "datasources.properties";

	U getProcessedJson();
	void saveCollection(U processedCollection);

	default String getDataSourceLocation(String key) throws IOException {
		Properties appProps = new Properties();
		appProps.load(new FileInputStream(Thread.currentThread().getContextClassLoader().getResource(dataSourceProperty).getPath()));
		return appProps.getProperty(key);
	}

}


