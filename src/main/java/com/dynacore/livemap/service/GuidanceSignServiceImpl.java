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

import com.dynacore.livemap.entity.hibernate.GuidanceSignLogData;
import com.dynacore.livemap.entity.jsonrepresentations.guidancesign.FeatureCollection;
import com.dynacore.livemap.repository.GuidanceSignRepository;


@Service("guidanceSignService")
public class GuidanceSignServiceImpl implements GuidanceSignService {
	
	@Autowired 
	private GuidanceSignRepository guidanceSignRepository;
	private FeatureCollection json;

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

	public GuidanceSignServiceImpl() {		
		latestPubdate = new String();
		currentPubdate = new String();
		ScheduledExecutorService exec = Executors.newSingleThreadScheduledExecutor();
		exec.scheduleAtFixedRate(new Runnable() {
		  
		  @Override		  
		  public void run() { 
			RestTemplate restTemplate = createRestTemplate();
			try {								
					json = restTemplate.getForObject("http://www.trafficlink-online.nl/trafficlinkdata/wegdata/IDPA_GuidanceSign.GeoJSON", FeatureCollection.class);
				//	saveCollection(json);	 //XXX implement hibernate one to many				
					customizeJson(json); //Customize Json for frontend.
				
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
		return json;
	}
		
	@Override
	public GuidanceSignLogData save(GuidanceSignLogData guidanceSign) {
		return guidanceSignRepository.save(guidanceSign);
	}

	@Override
	@Transactional
	public void saveCollection(FeatureCollection fc) {				
		for(int i=0; i < fc.getFeatures().size(); i++) {		
				GuidanceSignLogData property = new GuidanceSignLogData(
						fc.getFeatures().get(i).getProperties().getName(),
						fc.getFeatures().get(i).getProperties().getPubDate(), 
						fc.getFeatures().get(i).getProperties().getState()

				);				
				//only store logdata at start of the application or if it has changed.
				if( latestPubdate.isEmpty() ||  ! latestPubdate.equals( property.getPubDate() )) {				
		//			save(property); //XXX Implement one (GuidanceSign) to many (Displays) with hibernate..
					currentPubdate = property.getPubDate(); //TODO: Checken of currentPubdate voor latestpubdate viel.
				}
			}		
			latestPubdate = currentPubdate;		
		}		
	}


