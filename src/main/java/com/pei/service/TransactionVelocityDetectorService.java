package com.pei.service;

import org.springframework.stereotype.Service;
import com.pei.config.VelocityTransactionsProperties;

@Service
public class TransactionVelocityDetectorService {
    private final VelocityTransactionsProperties config;

    public TransactionVelocityDetectorService(VelocityTransactionsProperties config) {
        this.config = config;
    }

    public Integer getIndividuoMinutesRange () {
        return config.getIndividuoMinutesRange();
    }

    public Integer getEmpresaMinutesRange () {
        return config.getEmpresaMinutesRange();
    }

    public Integer getIndividuoMaxTransactions () {
        return config.getIndividuoMaxTransactions();
    }

    public Integer getEmpresaMaxTransactions () {
        return config.getEmpresaMaxTransactions();
    }

}
