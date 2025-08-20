package com.pei.dto;

import com.pei.domain.User.User;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

//manyToOne con usuario
@Entity
public class Purchase {

    @Id 
    private Long id;
    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private User user;

    public Purchase(Long id, User user) {
        this.id = id;
        this.user = user;
    }

    public Purchase() {}
}
