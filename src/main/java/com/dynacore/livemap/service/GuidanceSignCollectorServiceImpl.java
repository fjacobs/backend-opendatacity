package com.dynacore.livemap.service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.dynacore.livemap.entity.jsonrepresentations.parking.ParkingPlace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import com.dynacore.livemap.entity.hibernate.GuidanceSignLogData;
import com.dynacore.livemap.repository.GuidanceSignRepository;
import com.dynacore.livemap.entity.jsonrepresentations.GeoJsonCollection;
import com.dynacore.livemap.entity.jsonrepresentations.guidancesign.GuidanceSign;

@Service("guidanceSignService")
public class GuidanceSignCollectorServiceImpl implements TrafficDataCollectorService<GeoJsonCollection<GuidanceSign>> {
	
	@Autowired 
	private GuidanceSignRepository guidanceSignRepository;
	private GeoJsonCollection json;

	private String latestPubdate, currentPubdate;
	private int updateInterval = 60; 
	
	//Returns resttemplate that supports different mediatypes	
	private RestTemplate createRestTemplate() {
		RestTemplate restTemplate = new RestTemplate();
		MappingJackson2HttpMessageConverter jacksonMessageConverter = new MappingJackson2HttpMessageConverter();			
		List<MediaType> supportedMediaTypes = new ArrayList<>();
		supportedMediaTypes.add(MediaType.ALL);			
		jacksonMessageConverter.setSupportedMediaTypes(supportedMediaTypes);			
		restTemplate.getMessageConverters().add(jacksonMessageConverter);
		return restTemplate;
	}	

	public GuidanceSignCollectorServiceImpl() {
		latestPubdate = "";
		currentPubdate = "";
		ScheduledExecutorService exec = Executors.newSingleThreadScheduledExecutor();
		exec.scheduleAtFixedRate(new Runnable() {
		  
		  @Override		  
		  public void run() { 
			RestTemplate restTemplate = createRestTemplate();
			try {								
					//json = restTemplate.getForObject( getDataSourceUrl(GuidanceSign.class.getSimpleName()), GeoJsonCollection.class);
					json = restTemplate.exchange( getDataSourceUrl(GuidanceSign.class.getSimpleName()),
											   HttpMethod.GET,	null,
											   new ParameterizedTypeReference<GeoJsonCollection<ParkingPlace>>() {}).getBody();


					customizeJson(json); //Customize Json for frontend.
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		  }
		}, 0, updateInterval, TimeUnit.SECONDS);	
	}	
	
	//This method adds stuff to the inserted json 
	private GeoJsonCollection<GuidanceSign> customizeJson(GeoJsonCollection<GuidanceSign> top) {

		return top;
	}
	
	public GeoJsonCollection<GuidanceSign> getProcessedJson() {
		return json;
	}

	@Override
	@Transactional
	public void saveCollection(GeoJsonCollection<GuidanceSign> fc) {
		for(int i=0; i < fc.getFeatures().size(); i++) {		
				GuidanceSignLogData property = new GuidanceSignLogData(
						fc.getFeatures().get(i).getProperties().getName(),
						fc.getFeatures().get(i).getProperties().getPubDate(), 
						fc.getFeatures().get(i).getProperties().getState()

				);				
				//only store logdata at start of the application or if it has changed.
				if( latestPubdate.isEmpty() ||  ! latestPubdate.equals( property.getPubDate() )) {
					guidanceSignRepository.save(property);
					currentPubdate = property.getPubDate(); //TODO: Checken of currentPubdate voor latestpubdate viel.
				}
			}		
			latestPubdate = currentPubdate;		
		}		
	}


