package com.pei.service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.pei.repository.AccountParamsRepository;

@Service
public class AccountParamsService {

    private final AccountParamsRepository accountParamsRepository;
    private final Map<String, String> cacheParams = new ConcurrentHashMap<>();

    public AccountParamsService(AccountParamsRepository accountParamsRepository) {
        this.accountParamsRepository = accountParamsRepository;
        loadCache();
    }

    @Scheduled(fixedRate = 600_000) // cada 10 minutos
    public void loadCache() {
        cacheParams.clear();
        // accountParamsRepository.findAll().stream() //DESCOMENTAR TODO AL IMPLEMENTAR
        // BD
        // .filter(AccountParam::isActive)
        // .forEach(param -> cacheParams.put(param.getKeyName(), param.getValue()));
        System.out.println("[AccountParamsService] Cache actualizado: " + cacheParams);
    }

    public Integer getLimiteAlertaAltoRiesgoIndividuo() {
        return Integer.valueOf(cacheParams.getOrDefault("limite_alerta_alto_riesgo_individuo", "1"));
    }

    public Integer getLimiteAlertaAltoRiesgoEmpresa() {
        return Integer.valueOf(cacheParams.getOrDefault("limite_alerta_alto_riesgo_empresa", "2"));
    }

}
