package com.pei.domain;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String risk;

    @Column(nullable = false)
    private String profile;

    @Column(nullable = false)
    private BigDecimal averageMonthlySpending;

    @OneToMany(mappedBy = "owner")
    private List<Account> accounts = new ArrayList<>();

    public User() {
    }

    public Long getId() {
        return id;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRisk() {
        return risk;
    }

    public void setRisk(String risk) {
        this.risk = risk;
    }

    public List<Account> getAccounts() {
        return new ArrayList<>(accounts);
    }

    public void setAccounts(List<Account> accounts) {
        this.accounts = accounts;
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

}
