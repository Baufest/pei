package com.pei.domain;

import jakarta.persistence.*;

//manyToOne con usuario
@Entity
public class Chargeback {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    User user;

    public Chargeback(Long id, User user) {
        this.id = id;
        this.user = user;
    }

    public Chargeback() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
