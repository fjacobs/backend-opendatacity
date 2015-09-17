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

import com.dynacore.livemap.entity.hibernate.ParkingLogData;
import com.dynacore.livemap.entity.jsonrepresentations.parking.FeatureCollection;
import com.dynacore.livemap.repository.ParkingPlaceRepository;




//@Component is equivalent to
//
//<bean>
//@Service, @Controller , @Repository = {@Component + some more special functionality}
//
//That mean Service,Controller and Repository are functionally the same.
//
//The three annotations are used to separate "Layers" in your application,
//
//Controllers just do stuff like dispatching, forwarding, calling service methods etc.
//Service Hold business Logic, Calculations etc.
//Repository are the DAOs(Data Access Objects), they access the database directly.
//Now you may ask why separate them:(I assume you know AOP-Aspect Oriented Programming)
//
//Lets say you want to Monitors the Activity of the DAO Layer only. You will write an Aspect(A class) class that does some logging before and after every method of your DAO is invoked, you are able to do that using AOP as you have three distinct Layers and are not mixed.
//
//So you can do logging of DAO "around", "before" or "after" the DAO methods. You could do that because you had a DAO in the first place. What you just achieved is Separation of concerns or tasks.
//
//Imagine if there were only one annotation @Controller, then this component will have dispatching, business logic and accessing database all mixed, so dirty code!
//
//Above mentioned is one very common scenario, there are many more use cases of why to use three annotations.
//



@Service("parkingPlaceService")
public class ParkingPlaceServiceImpl implements ParkingPlaceService {
	
	@Autowired 
	private ParkingPlaceRepository parkingPlaceRepository;
	private FeatureCollection parkingJson;

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

	public ParkingPlaceServiceImpl() {		
		latestPubdate = new String();
		currentPubdate = new String();
		ScheduledExecutorService exec = Executors.newSingleThreadScheduledExecutor();
		exec.scheduleAtFixedRate(new Runnable() {
		  
		  @Override		  
		  public void run() { 
			RestTemplate restTemplate = createRestTemplate();
			try {								
					parkingJson = restTemplate.getForObject("http://www.trafficlink-online.nl/trafficlinkdata/wegdata/IDPA_ParkingLocation.GeoJSON", FeatureCollection.class);
					saveCollection(parkingJson);					
					customizeJson(parkingJson); //Customize Json for frontend.
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		  }
		}, 0, updateInterval, TimeUnit.SECONDS);	
	}	
	
	//This method adds stuff to the inserted json (only percentage for now)
	private FeatureCollection customizeJson(FeatureCollection top) {
		try {
			
			//Calculate percentage (How full is the parking) and insert into new json format
			for(int i=0; i < top.getFeatures().size(); i++) {
		
				int capacity = Integer.parseInt(top.getFeatures().get(i).getProperties().getShortCapacity());
				int parkedCars = (capacity - Integer.parseInt(top.getFeatures().get(i).getProperties().getFreeSpaceShort()));
				int percentage = (int) ((float) (parkedCars *100) / capacity);
				
				top.getFeatures().get(i).getProperties().setPercentage(percentage);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return top;
	}
	
	public FeatureCollection getProcessedJson() {
		return parkingJson;
	}
		
	@Override
	public ParkingLogData save(ParkingLogData parkingPlace) {
		return parkingPlaceRepository.save(parkingPlace);
	}

	@Override
	@Transactional
	public void saveCollection(FeatureCollection fc) {				
		for(int i=0; i < fc.getFeatures().size(); i++) {		
				ParkingLogData property = new ParkingLogData(
						fc.getFeatures().get(i).getProperties().getName(),
						fc.getFeatures().get(i).getProperties().getPubDate(), 
						fc.getFeatures().get(i).getProperties().getType(), 
						fc.getFeatures().get(i).getProperties().getState(), 
						fc.getFeatures().get(i).getProperties().getFreeSpaceShort(), 
						fc.getFeatures().get(i).getProperties().getFreeSpaceLong(), 
						fc.getFeatures().get(i).getProperties().getShortCapacity(), 
						fc.getFeatures().get(i).getProperties().getLongCapacity()
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


