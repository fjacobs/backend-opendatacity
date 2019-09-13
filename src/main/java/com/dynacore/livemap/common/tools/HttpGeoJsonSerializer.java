package com.dynacore.livemap.common.tools;

import com.dynacore.livemap.common.model.FeatureCollection;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.ResolvableType;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class HttpGeoJsonSerializer<T>  {

    public <T> FeatureCollection marshallFromUrl(String url, Class<T> clazz) {
        RestTemplate client = createRestClient();

        Type resolvableType = ResolvableType.forClassWithGenerics(FeatureCollection.class, clazz).getType();
        ResponseEntity<FeatureCollection> responseEntity = null;
        try{
              responseEntity = client.exchange(url,
                    HttpMethod.GET,
                    null,
                    ParameterizedTypeReference.forType(resolvableType), clazz);
        } catch(Exception error) {
            error.printStackTrace();
        }

        return  responseEntity.getBody();
    }

    private RestTemplate createRestClient() {
        RestTemplate restTemplate = new RestTemplate();

        List<MediaType> supportedMediaTypes = new ArrayList<>();
        supportedMediaTypes.add(MediaType.ALL);

        MappingJackson2HttpMessageConverter jacksonMessageConverter = new MappingJackson2HttpMessageConverter();
        jacksonMessageConverter.setSupportedMediaTypes(supportedMediaTypes);

        restTemplate.getMessageConverters().add(jacksonMessageConverter);
        return restTemplate;
    }
}
