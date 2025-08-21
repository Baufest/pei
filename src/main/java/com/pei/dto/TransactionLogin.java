package com.pei.dto;

import com.pei.domain.*;

public class TransactionLogin {

    private Login login;
    private Transaction transaction;

    public TransactionLogin(Login login, Transaction transaction) {
        this.login = login;
        this.transaction = transaction;
    }

    // Getters y Setters
    public Login getLogin() {
        return login;
    }

    public void setLogin(Login login) {
        this.login = login;
    }

    public Transaction getTransaction() {
        return transaction;
    }

    public void setTransaction(Transaction transaction) {
        this.transaction = transaction;
    }
}

