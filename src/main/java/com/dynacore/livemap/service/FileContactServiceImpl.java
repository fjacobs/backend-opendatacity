package com.dynacore.livemap.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.web.client.RestTemplate;

import com.dynacore.livemap.entity.hibernate.FileContactLogData;
import com.dynacore.livemap.entity.jsonrepresentations.filecontact.FeatureCollection;
import com.dynacore.livemap.repository.FileContactRepository;

@Service("fileContactService")
public class FileContactServiceImpl implements FileContactService {
	
	@Autowired 
	private FileContactRepository fileContactRepository;
	private FeatureCollection fileContactJson;

	private String latestPubdate, currentPubdate;
	private int updateInterval = 60; 
	
	//Returns resttemplate that supports different mediatypes	
	private RestTemplate createRestTemplate() {
		RestTemplate restTemplate = new RestTemplate();
		MappingJackson2HttpMessageConverter jacksonMessageConverter = new MappingJackson2HttpMessageConverter();			
		List<MediaType> supportedMediaTypes = new ArrayList<MediaType>(); 
		supportedMediaTypes.add(MediaType.ALL);			
		jacksonMessageConverter.setSupportedMediaTypes(supportedMediaTypes);			
		List<HttpMessageConverter<?>> messageConverters = new ArrayList<HttpMessageConverter<?>>(); 
		messageConverters.add(jacksonMessageConverter);		
		restTemplate.getMessageConverters().add(jacksonMessageConverter);
		return restTemplate;
	}
	
	public FileContactServiceImpl() {		
		latestPubdate = new String();
		currentPubdate = new String();
		ScheduledExecutorService exec = Executors.newSingleThreadScheduledExecutor();
		exec.scheduleAtFixedRate(new Runnable() {
		  
		  @Override		  
		  public void run() { 
			RestTemplate restTemplate = createRestTemplate();
						try {								
					fileContactJson = restTemplate.getForObject("http://www.trafficlink-online.nl/trafficlinkdata/wegdata/FileContact.GeoJSON", FeatureCollection.class );
					saveCollection(fileContactJson);					
					customizeJson(fileContactJson);
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		  }
		}, 0, updateInterval, TimeUnit.SECONDS);	
	}	
	
	//This method adds stuff to the inserted json 
	private FeatureCollection customizeJson(FeatureCollection top) {
		try {
			
		
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return top;
	}
	
	public FeatureCollection getProcessedJson() {
		return fileContactJson;
	}
		
	@Override
	public FileContactLogData save(FileContactLogData fileContact) {
		return fileContactRepository.save(fileContact);
	}

	@Override
	@Transactional
	public void saveCollection(FeatureCollection fc) {
		
	//	System.out.println("Saving featurecollection: " + (System.currentTimeMillis() / 1000L));
		
		for(int i=0; i < fc.getFeatures().size(); i++) {
		
				FileContactLogData property = new FileContactLogData(
						fc.getFeatures().get(i).getProperties().get(0).getName(),
						fc.getFeatures().get(i).getProperties().get(0).getTimestamp(), 
						fc.getFeatures().get(i).getProperties().get(0).getState()
	
				);
				
			//Only store logdata at start of the application
			//Or when the update time has actually changed
			if( latestPubdate.isEmpty() ||  ! latestPubdate.equals( property.getPubDate() )) {				
				save(property);
					currentPubdate = property.getPubDate(); //TODO: Checken of currentPubdate voor latestpubdate viel.
				}
			}
		
			latestPubdate = currentPubdate;
		
		}		
	}
