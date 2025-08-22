package com.pei.domain.User;

import com.pei.domain.Device;
import com.pei.domain.Country;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.HashSet;
import java.util.Objects;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import com.pei.domain.Account.Account;
import com.pei.domain.TimeRange;
import com.pei.dto.Chargeback;
import com.pei.dto.Purchase;

@Entity @Table(name = "client")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column
    private String phoneNumber;

    @Column(nullable = false)
    private String risk;

    @Column(nullable = false)
    private String profile;

    @Column
    @Enumerated(EnumType.STRING)
    private ClientType clientType;

    @Column(nullable = false)
    private BigDecimal averageMonthlySpending;

    @Column
    private LocalDate creationDate;

    @OneToMany(mappedBy = "owner")
    private List<Account> accounts = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<Purchase> purchases = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<Chargeback> chargebacks = new ArrayList<>();

    @Column(nullable = false, unique = true) // email único y obligatorio
    private String email;
    
    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER, orphanRemoval = true)
    private Set<Device> devices = new HashSet<>();

    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER, orphanRemoval = true)
    private Set<Country> countries = new HashSet<>();

    @Embedded
    private TimeRange avgTimeRange;


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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public LocalDate getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDate creationDate) {
        this.creationDate = creationDate;
    }

    public ClientType getClientType() {
        return clientType;
    }

    public void setClientType(ClientType clientType) {
        this.clientType = clientType;
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

    public TimeRange getAvgTimeRange() {
        return avgTimeRange;
    }

    public void setAvgTimeRange(TimeRange avgTimeRange) {
        this.avgTimeRange = avgTimeRange;
    }

    public Set<Device> getDevices() {
        return devices;
    }
    public void setDevices(Set<Device> devices) {
        this.devices = devices;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id) && Objects.equals(name, user.name) && Objects.equals(risk, user.risk) && Objects.equals(profile, user.profile) && clientType == user.clientType && Objects.equals(averageMonthlySpending, user.averageMonthlySpending) && Objects.equals(creationDate, user.creationDate) && Objects.equals(email, user.email) && Objects.equals(avgTimeRange, user.avgTimeRange);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, risk, profile, clientType, averageMonthlySpending, creationDate, email, avgTimeRange);
    }
}
