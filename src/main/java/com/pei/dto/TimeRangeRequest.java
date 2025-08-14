package com.pei.dto;

import com.pei.domain.Transaction;
import java.util.List;

public class TimeRangeRequest {
    private List<Transaction> transactions;
    private Transaction newTransaction;

    public List<Transaction> getTransactions() {
        return transactions;
    }
    public void setTransactions(List<Transaction> transactions) {
        this.transactions = transactions;
    }
    public Transaction getNewTransaction() {
        return newTransaction;
    }
    public void setNewTransaction(Transaction newTransaction) {
        this.newTransaction = newTransaction;
    }
}
