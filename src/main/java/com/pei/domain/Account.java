package com.pei.domain;

import jakarta.persistence.*;

import java.util.Objects;

@Entity @Table(name = "Account")
public class Account {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "owner", referencedColumnName = "id", nullable = false)
    private User owner;

    public Long getId() {
        return id;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public Account(User owner) {
        this.owner = owner;
    }

    public Account(Long id, User owner) {
        this.id = id;
        this.owner = owner;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Account() {}

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Account account = (Account) o;
        return Objects.equals(id, account.id) && Objects.equals(owner, account.owner);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, owner);
    }
}
