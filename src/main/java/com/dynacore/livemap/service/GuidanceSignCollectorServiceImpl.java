package com.dynacore.livemap.service;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
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

	private GeoJsonCollection geoJsonCollection;
	private String latestPubdate, currentPubdate;
	private int updateInterval = 60;

	public GuidanceSignCollectorServiceImpl() {
		ScheduledExecutorService exec = Executors.newSingleThreadScheduledExecutor();
		exec.scheduleAtFixedRate(() -> {
		  RestTemplate restTemplate = createRestClient();
		  try {
				  geoJsonCollection = restTemplate.exchange( getDataSourceUrl(GuidanceSign.class.getSimpleName()),
											 HttpMethod.GET,	null,
											 new ParameterizedTypeReference<GeoJsonCollection<GuidanceSign>>() {}).getBody();
				  customizeJson(geoJsonCollection); //Customize Json for frontend.
		  } catch (Exception e) {
			  e.printStackTrace();
		  }
		}, 0, updateInterval, TimeUnit.SECONDS);
	}	
	
	private GeoJsonCollection<GuidanceSign> customizeJson(GeoJsonCollection<GuidanceSign> top) {
		return top;
	}
	
	public GeoJsonCollection<GuidanceSign> getProcessedJson() {
		return geoJsonCollection;
	}

	@Override
	@Transactional
	public void saveCollection(GeoJsonCollection<GuidanceSign> fc) {
		for(int i=0; i < fc.getFeatures().size(); i++) {		
				GuidanceSignLogData property = new GuidanceSignLogData(
						fc.getFeatures().get(i).getName(),
						fc.getFeatures().get(i).getPubDate(),
						fc.getFeatures().get(i).getState()
				);				
				//only store logdata at start of the application or if it has changed.
				if( latestPubdate.isEmpty() ||  ! latestPubdate.equals( property.getPubDate() )) {
					guidanceSignRepository.save(property);
					latestPubdate = property.getPubDate();
				}
			}
			latestPubdate = currentPubdate;
		}		
	}


