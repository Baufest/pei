package com.pei.domain;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.pei.dto.Chargeback;
import com.pei.dto.Purchase;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import org.hibernate.annotations.CreationTimestamp;

@Entity @Table(name = "client")
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

    @CreationTimestamp
    private LocalDate userSince;

    @OneToMany(mappedBy = "owner")
    private List<Account> accounts = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<Purchase> purchases = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<Chargeback> chargebacks = new ArrayList<>();

    public User() {}

    public User(Long id) {
        this.id = id;
        this.accounts = new java.util.ArrayList<>();
    }
    public User(Long id, List<Account> accounts) {
        this.id = id;
        this.accounts = accounts;
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

    public void addAccounts(Account account) {
        this.accounts.add(account);
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

    public LocalDate getUserSince() {
        return userSince;
    }

    public void setUserSince(LocalDate userSince) {
        this.userSince = userSince;
    }

    public BigDecimal getAverageMonthlySpending() {
        return averageMonthlySpending;
    }

    public void setAverageMonthlySpending(BigDecimal averageMonthlySpending) {
        this.averageMonthlySpending = averageMonthlySpending;
    }

    public List<Chargeback> getChargebacks() {
        return chargebacks;
    }

    public void addChargebacks(List<Chargeback> chargebacks) {
        this.chargebacks.addAll(chargebacks);
    }

    public void addPurchases(List<Purchase> purchases) {
        this.purchases.addAll(purchases);
    }

    public List<Purchase> getPurchases() {
        return purchases;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id) && Objects.equals(name, user.name) && Objects.equals(risk, user.risk) && Objects.equals(profile, user.profile) && Objects.equals(averageMonthlySpending, user.averageMonthlySpending) && Objects.equals(userSince, user.userSince) && Objects.equals(accounts, user.accounts) && Objects.equals(purchases, user.purchases) && Objects.equals(chargebacks, user.chargebacks);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, risk, profile, averageMonthlySpending, userSince, accounts, purchases, chargebacks);
    }
}
