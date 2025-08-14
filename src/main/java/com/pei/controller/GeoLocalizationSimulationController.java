package com.pei.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pei.service.GeoSimService;

@RestController
@RequestMapping("/ip")
public class GeoLocalizationSimulationController {


    private GeoSimService geoSimService;

    public GeoLocalizationSimulationController(GeoSimService geoService) {
        this.geoSimService = geoService;
    }

    @GetMapping("/country/{ip}")
    public ResponseEntity<String> getIpInfo(@PathVariable String ip) {
        String country = geoSimService.getCountryFromIP(ip);
        return ResponseEntity.ok(country);
    }
}
