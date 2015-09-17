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

import com.dynacore.livemap.entity.hibernate.BrugContactLogData;
import com.dynacore.livemap.entity.jsonrepresentations.brugcontact.FeatureCollection;
import com.dynacore.livemap.repository.BrugContactRepository;


@Service("BrugContactService")
public class BrugContactServiceImpl implements BrugContactService {
	
	@Autowired 
	private BrugContactRepository brugContactRepository;
	private FeatureCollection brugContactJson;

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

	public BrugContactServiceImpl() {		
		latestPubdate = new String();
		currentPubdate = new String();
		ScheduledExecutorService exec = Executors.newSingleThreadScheduledExecutor();
		exec.scheduleAtFixedRate(new Runnable() {
		  
		  @Override		  
		  public void run() { 
			RestTemplate restTemplate = createRestTemplate();
			try {								
					brugContactJson = restTemplate.getForObject("http://www.trafficlink-online.nl/trafficlinkdata/wegdata/BrugContact.GeoJSON", FeatureCollection.class);
					saveCollection(brugContactJson);					
					customizeJson(brugContactJson); //Customize Json for frontend.
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		  }
		}, 0, updateInterval, TimeUnit.SECONDS);	
	}	
	
	//This method adds stuff to the inserted json (only percentage for now)
	private FeatureCollection customizeJson(FeatureCollection top) {
		try {
			
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return top;
	}
	
	public FeatureCollection getProcessedJson() {
		return brugContactJson;
	}
		
	@Override
	public BrugContactLogData save(BrugContactLogData brugContact) {
		return brugContactRepository.save(brugContact);
	}

	@Override
	@Transactional
	public void saveCollection(FeatureCollection fc) {				
		for(int i=0; i < fc.getFeatures().size(); i++) {		
				BrugContactLogData property = new BrugContactLogData(
						fc.getFeatures().get(i).getProperties().get(0).getName(),
						fc.getFeatures().get(i).getProperties().get(0).getTimestamp(), 
						fc.getFeatures().get(i).getProperties().get(0).getState()
				);				
				//only store logdata at start of the application or if it has changed.
				if( latestPubdate.isEmpty() ||  ! latestPubdate.equals( property.getPubDate() )) {				
					save(property);
					currentPubdate = property.getPubDate(); //TODO: Checken of currentPubdate voor latestpubdate viel.
				}
			}		
			latestPubdate = currentPubdate;		
		}		
	}


