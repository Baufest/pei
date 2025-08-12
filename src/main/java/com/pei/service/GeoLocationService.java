package com.pei.service;

import java.util.Map;

import org.springframework.stereotype.Service;

@Service
public class GeoLocationService {
    
    /* Valores de prueba */
    private static final Map<String, String> IP_FOR_COUNTRY = Map.of("200.1.1.1", "AR", "181.45.67.89", "UY",
            "50.23.45.67", "US");

    public String getCountryFromIP(String ip) {
        String ret = IP_FOR_COUNTRY.get(ip);

        return ret.length() > 1 ? ret : "DESCONOCIDO";
    }
}
