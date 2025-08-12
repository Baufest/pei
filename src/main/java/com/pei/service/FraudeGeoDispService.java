package com.pei.service;

import org.springframework.stereotype.Service;

import com.pei.repository.UserRepository;

@Service
public class FraudeGeoDispService {

    private UserRepository userRepository;
    private GeoLocationService geoLocationService;

    public FraudeGeoDispService(UserRepository userRepository, GeoLocationService geoLocationService) {
        this.userRepository = userRepository;
        this.geoLocationService = geoLocationService;
    }

    public String verifyFraudOfDeviceAndGeo() {
        
    }
}
