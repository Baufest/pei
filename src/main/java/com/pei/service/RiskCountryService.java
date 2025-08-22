package com.pei.service;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.pei.config.TransferenciaInternacionalProperties;
import com.pei.repository.RiskCountryRepository;

@Service
public class RiskCountryService {

    private final RiskCountryRepository riskCountryRepository;
    // Cache local
    private final Set<String> cacheRiskCountries = ConcurrentHashMap.newKeySet();

    private List<String> riskCountries;

    public RiskCountryService(RiskCountryRepository riskCountryRepository, TransferenciaInternacionalProperties internationalCountryConfig) {
        this.riskCountryRepository = riskCountryRepository;
        this.riskCountries = internationalCountryConfig.getPaisesRiesgo();
        loadCache();
    }

    // Verifica si un país es de riesgo
    public boolean isRiskCountry(String pais) {
        return cacheRiskCountries.contains(pais);
    }

    // Carga o recarga el cache desde la BD
    @Scheduled(fixedRate = 600_000) // cada 10 minutos
    public void loadCache() {
        //List<RiskCountry> countries = riskCountryRepository.findByActiveTrue(); DESCOMENTAR CUANDO IMPLEMENTEMOS BD
        cacheRiskCountries.clear();
        for (String p : this.riskCountries) { //String cambia por RiskCountry al implementar BD
            cacheRiskCountries.add(p); //p cambia a p.getName() al implementar BD
        }
        System.out.println("Cache de países de riesgo actualizado: " + cacheRiskCountries);
    }
}
