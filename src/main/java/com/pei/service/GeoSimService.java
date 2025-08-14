package com.pei.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.pei.dto.GeoServiceResponse;

@Service
public class GeoSimService {

    private final RestTemplate restTemplate;

    public GeoSimService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String getCountryFromIP(String ip) {
        String url = "http://ip-api.com/json/" + ip;
        GeoServiceResponse response = restTemplate.getForObject(url, GeoServiceResponse.class);
        return (response != null) ? response.getCountry() : null;
    }
}

