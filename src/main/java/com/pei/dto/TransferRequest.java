package com.pei.dto;

import com.pei.domain.Account;
import com.pei.domain.Transaction;

public class TransferRequest {

    private Account destinationAccount;
    private Transaction currentTransaction;

    public Account getDestinationAccount() {
        return destinationAccount;
    }

    public void setDestinationAccount(Account destinationAccount) {
        this.destinationAccount = destinationAccount;
    }

    public Transaction getCurrentTransaction() {
        return currentTransaction;
    }

    public void setCurrentTransaction(Transaction currentTransaction) {
        this.currentTransaction = currentTransaction;
    }

}
