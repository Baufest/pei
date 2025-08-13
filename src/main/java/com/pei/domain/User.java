package com.pei.domain;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity @Table(name = "Client")
public class User {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "owner")
    private List<Account> accounts;

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
