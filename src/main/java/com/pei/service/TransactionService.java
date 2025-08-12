package com.pei.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pei.dto.Alert;
import com.pei.repository.ChargebackRepository;
import com.pei.repository.PurchaseRepository;

@Service
public class TransactionService {
    
    private final ChargebackRepository chargebackRepository;
    private final PurchaseRepository purchaseRepository;

    @Autowired
    public TransactionService(ChargebackRepository chargebackRepository, PurchaseRepository purchaseRepository) {
        this.chargebackRepository = chargebackRepository;
        this.purchaseRepository = purchaseRepository;}
  
    public Alert getChargebackFraudAlert(Long userId) {
        int numberOfChargebacks = chargebackRepository.findByUserId(userId).size();
        int numberOfPurchases = purchaseRepository.findByUserId(userId).size();
        Alert chargebackFraudAlert = null;

        if (numberOfPurchases == 0 && numberOfChargebacks > 0) {
            chargebackFraudAlert = new Alert(
                    userId, 
                    "Chargeback fraud detected for user " + userId
                );
        }

        if (numberOfPurchases > 0) {
            if ((double) numberOfChargebacks / numberOfPurchases > 0.1) {
                chargebackFraudAlert = new Alert(
                    userId, 
                    "Chargeback fraud detected for user " + userId
                );
            }
        }

        return chargebackFraudAlert;
    }}
