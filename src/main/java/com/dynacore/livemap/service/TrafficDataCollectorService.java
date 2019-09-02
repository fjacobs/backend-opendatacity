package com.dynacore.livemap.service;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

/**
 * @param <U> Processed Endpoint JSON representation
 */

public interface TrafficDataCollectorService<U> {
	String dataSourceProperty = "datasources.properties";

	U getProcessedJson();
	
	void saveCollection(U processedCollection);

	default String getDataSourceUrl(String key) throws IOException {
		Properties appProps = new Properties();
		appProps.load(new FileInputStream(Thread.currentThread().getContextClassLoader().getResource(dataSourceProperty).getPath()));
		return appProps.getProperty(key);
	}

	default RestTemplate createRestClient() {
		RestTemplate restTemplate = new RestTemplate();

		List<MediaType> supportedMediaTypes = new ArrayList<>();
		supportedMediaTypes.add(MediaType.ALL);

		MappingJackson2HttpMessageConverter jacksonMessageConverter = new MappingJackson2HttpMessageConverter();
		jacksonMessageConverter.setSupportedMediaTypes(supportedMediaTypes);

		restTemplate.getMessageConverters().add(jacksonMessageConverter);
		return restTemplate;
	}
}


