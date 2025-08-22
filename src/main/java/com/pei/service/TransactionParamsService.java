package com.pei.service;

import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.pei.repository.TransactionParamsRepository;

@Service
public class TransactionParamsService {

    private final TransactionParamsRepository transactionParamRepository;

    private final Map<String, String> cacheParams = new ConcurrentHashMap<>();

    public TransactionParamsService(TransactionParamsRepository transactionParamRepository) {
        this.transactionParamRepository = transactionParamRepository;
        loadCache();
    }

    @Scheduled(fixedRate = 600_000) //cada 10 minutos
    public void loadCache() {
        cacheParams.clear();
        // transactionParamRepository.findAll().stream() //DESCOMENTAR TODO AL IMPLEMENTAR BD
        //         .filter(TransactionParam::isActive)
        //         .forEach(param -> cacheParams.put(param.getKeyName(), param.getValue()));
        System.out.println("[TransactionParamsService] Cache actualizado: " + cacheParams);
    }

    public BigDecimal getMontoAlertaInternacional() {
        return new BigDecimal(cacheParams.getOrDefault("monto_alerta_internacional", "50000"));
    }

}
