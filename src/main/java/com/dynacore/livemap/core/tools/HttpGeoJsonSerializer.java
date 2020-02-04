package com.dynacore.livemap.core.tools;

import com.dynacore.livemap.core.model.FeatureCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.ResolvableType;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class HttpGeoJsonSerializer<T> {
  private Logger logger = LoggerFactory.getLogger(HttpGeoJsonSerializer.class);

  public <T> FeatureCollection marshallFromUrl(String url, Class<T> clazz) {
    RestTemplate client = createRestClient();

    Type resolvableType =
        ResolvableType.forClassWithGenerics(FeatureCollection.class, clazz).getType();
    ResponseEntity<FeatureCollection> responseEntity = null;
    try {
      responseEntity =
          client.exchange(
              url, HttpMethod.GET, null, ParameterizedTypeReference.forType(resolvableType), clazz);
    } catch (RestClientException error) {
      logger.error("Could not get data from: " + url + " error: " + error);
    } catch (HttpMessageConversionException | IllegalStateException error) {
      logger.error("Can't unmarshall check model.... ", error);
    }

    if (responseEntity == null) {
      throw new NullPointerException("Could not unmarshall from: " + url);
    }
    return responseEntity.getBody();
  }

  private RestTemplate createRestClient() {
    RestTemplate restTemplate = new RestTemplate();

    List<MediaType> supportedMediaTypes = new ArrayList<>();
    supportedMediaTypes.add(MediaType.ALL);

    MappingJackson2HttpMessageConverter jacksonMessageConverter =
        new MappingJackson2HttpMessageConverter();
    jacksonMessageConverter.setSupportedMediaTypes(supportedMediaTypes);

    restTemplate.getMessageConverters().add(jacksonMessageConverter);
    return restTemplate;
  }
}
