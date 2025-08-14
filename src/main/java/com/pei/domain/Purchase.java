package com.pei.domain;

import jakarta.persistence.*;

//manyToOne con usuario
@Entity
public class Purchase {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) // Uncomment if you want auto-generated IDs
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private User user;

    public Purchase(Long id, User user) {
        this.id = id;
        this.user = user;
    }

    public Purchase() {}

    public void setUser(User user) {
        this.user = user;
    }

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }
}
