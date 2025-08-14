package com.pei.dto;

import com.pei.domain.Transaction;
import com.pei.domain.User;

public class UserTransaction {
    
    private User user;
    private Transaction transaction;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Transaction getTransaction() {
        return transaction;
    }

    public void setTransaction(Transaction transaction) {
        this.transaction = transaction;
    }


}
    