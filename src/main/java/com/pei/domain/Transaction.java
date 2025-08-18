package com.pei.domain;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.*;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity @Table(name = "user_transaction")
public class Transaction {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private User user;

    @Column(nullable = false)
    private BigDecimal amount;

    @Column(nullable = false)
    private LocalDateTime date;

    @ManyToOne
    @JoinColumn(name = "source_account", referencedColumnName = "id", nullable = false)
    private Account sourceAccount;

    @ManyToOne
    @JoinColumn(name = "destination_account", referencedColumnName = "id", nullable = false)
    private Account destinationAccount;



    public Transaction() {}


    public Transaction(User user, BigDecimal amount) {
        this.user = user;
        this.amount = amount;

    }


    public Long getId() {
        return id;
    }


    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Account getDestinationAccount() {
        return destinationAccount;
    }
    public void setDestinationAccount(Account destinationAccount) {
        this.destinationAccount = destinationAccount;
    }
    public Account getSourceAccount() {
        return sourceAccount;
    }
    public void setSourceAccount(Account sourceAccount) {
        this.sourceAccount = sourceAccount;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Transaction that = (Transaction) o;
        return Objects.equals(id, that.id) && Objects.equals(user, that.user) && Objects.equals(amount, that.amount) && Objects.equals(date, that.date) && Objects.equals(sourceAccount, that.sourceAccount) && Objects.equals(destinationAccount, that.destinationAccount);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, user, amount, date, sourceAccount, destinationAccount);
    }
}
