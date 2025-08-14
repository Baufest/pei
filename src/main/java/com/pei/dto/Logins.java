package com.pei.dto;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
@Entity
public class Logins {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long userId;
    private String country;
    private LocalDateTime loginTime;

    public Logins() { }

    public Logins(Long userId, String country, LocalDateTime loginTime) {
        this.userId = userId;
        this.country = country;
        this.loginTime = loginTime;
    }

    public Logins(Long id, Long userId, String country, LocalDateTime loginTime) {
        this.id = id;
        this.userId = userId;
        this.country = country;
        this.loginTime = loginTime;
    }


    public String getCountry() {
        return country;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public LocalDateTime getLoginTime() {
        return loginTime;
    }

    public void setLoginTime(LocalDateTime loginTime) {
        this.loginTime = loginTime;
    }
}
