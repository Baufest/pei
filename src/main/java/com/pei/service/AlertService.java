package com.pei.service;

import com.pei.domain.*;
import com.pei.dto.Alert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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

    public Alert timeRangeAlert(List<Transaction> transactions, Transaction transaction) {
        Alert alerta = null;
        TimeRange timeRange = transactionService.getAvgTimeRange(transactions);

        int lastHour = transaction.getDateHour().getHour();
        Long transactionId = transaction.getId();

        if (lastHour < timeRange.getMinHour() || lastHour > timeRange.getMaxHour()) {
            alerta = new Alert(transactionId, "Transacción con ID = " + transactionId + ", realizada fuera del rango de horas promedio: " + timeRange.getMinHour() + " - " + timeRange.getMaxHour());
        }
        return alerta;
    }

}
