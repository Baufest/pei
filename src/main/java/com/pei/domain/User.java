package com.pei.domain;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;

import com.pei.dto.Chargeback;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity @Table(name = "Client")
public class User {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String profile;

    @Column(nullable = false)
    private BigDecimal averageMonthlySpending;

    @Column(nullable = false)
    private String risk;

    @CreationTimestamp
    private LocalDate creationDate;

    @Column(nullable = false)
    private String clientType;

    @OneToMany(mappedBy = "user")
    private List<Chargeback> chargebacks = new ArrayList<>();

    @OneToMany(mappedBy = "owner")
    private List<Account> accounts = new ArrayList<>();

    public User() { }

    public User(String profile, BigDecimal averageMonthlySpending, String risk, LocalDate creationDate, String clientType) {
        this.profile = profile;
        this.averageMonthlySpending = averageMonthlySpending;
        this.risk = risk;
        this.creationDate = creationDate;
        this.clientType = clientType;
    }

    public Long getId() {
        return id;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public BigDecimal getAverageMonthlySpending() {
        return averageMonthlySpending;
    }

    public void setAverageMonthlySpending(BigDecimal averageMonthlySpending) {
        this.averageMonthlySpending = averageMonthlySpending;
    }

    public String getRisk() {
        return risk;
    }

    public void setRisk(String risk) {
        this.risk = risk;
    }

    public LocalDate getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDate creationDate) {
        this.creationDate = creationDate;
    }

    public String getClientType() {
        return clientType;
    }

    public void setClientType(String clientType) {
        this.clientType = clientType;
    }

    public List<Chargeback> getChargebacks() {
        return chargebacks;
    }

    public void addChargebacks(List<Chargeback> chargebacks) {
        this.chargebacks.addAll(chargebacks);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Account> getAccounts() {
        return accounts;
    }

    public void setAccounts(List<Account> accounts) {
        this.accounts = new ArrayList<>(accounts);
    }

}
