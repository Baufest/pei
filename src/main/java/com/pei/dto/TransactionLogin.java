package com.pei.dto;

public class TransactionLogin {

    private Long idLogin;
    private Long idTransaction;

    public TransactionLogin() {}

    public TransactionLogin (Long idLogin, Long idTransaction) {
        this.idLogin = idLogin;
        this.idTransaction = idTransaction;
    }

    public Long getIdLogin() {
        return idLogin;
    }

    public void setIdLogin(Long idLogin) {
        this.idLogin = idLogin;
    }

    public Long getIdTransaction() {
        return idTransaction;
    }

    public void setIdTransaction(Long idTransaction) {
        this.idTransaction = idTransaction;
    }
}
