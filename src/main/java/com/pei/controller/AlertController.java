package com.pei.controller;

import org.springframework.web.bind.annotation.RestController;

import com.pei.dto.Alert;
import com.pei.dto.Logins;
import com.pei.service.GeolocalizationService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
@RequestMapping("/api")
public class AlertController {

    private GeolocalizationService geolocalizationService;

    public AlertController(GeolocalizationService geolocalizationService) {
        this.geolocalizationService = geolocalizationService;
    }

    @PostMapping("/alerta-dispositivo")
    public ResponseEntity<Alert> checkDeviceLocalization(@RequestBody Logins login) {
        try {
            if (login == null) {
                return ResponseEntity.badRequest().build();
            }
            Alert alerta = geolocalizationService.verifyFraudOfDeviceAndGeolocation(login);
            return ResponseEntity.ok(alerta);

        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

}
