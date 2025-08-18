package com.pei.dto;

import com.pei.domain.User;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class Chargeback {
    @Id
    Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    User user;

    public Chargeback(Long id, User user) {
        this.id = id;
        this.user = user;
    }

    public Chargeback() {}

}
