package com.pei.service;

import com.pei.dto.Alert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AlertService {

    @Autowired
    private  TransactionService transactionService;


    public Alert approvalAlert(Long transactionId){
        int approvalCount = transactionService.getApprovalCount(transactionId);
        Alert alerta = null;
        if(approvalCount > 2){
            alerta = new Alert(transactionId, "Transacción con ID = " + transactionId + " tiene más de 2 aprobaciones");
        }
        return alerta;
    }
}
