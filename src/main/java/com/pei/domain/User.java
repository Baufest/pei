package com.pei.domain;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Entity @Table(name = "Client")
public class User {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "owner")
    private List<Account> accounts;

    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER, orphanRemoval = true)
    private Set<Device> devices = new HashSet<>();

    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER, orphanRemoval = true)
    private Set<Country> countries = new HashSet<>();

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

    public List<Account> getAccounts() {
        return accounts;
    }

    public void addAccounts(Account account) {
        this.accounts.add(account);
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setAccounts(List<Account> accounts) {
        this.accounts = accounts;
    }
    
    

    public Set<Device> getDevices() {
        return devices;
    }

    public void addDevice(Device device) {
        device.setUser(this);
        devices.add(device);
    }

    public Set<Country> getCountries() {
        return countries;
    }

    public void addCountry(Country country) {
        country.setUser(this);
        countries.add(country);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id) && Objects.equals(accounts, user.accounts);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, accounts);
    }
}