package com.pei.domain;

import java.time.LocalDateTime;

import com.pei.domain.User.User;
import jakarta.persistence.*;

@Entity
public class Login {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "device_id", nullable = false)
    private Device device;

    @Column(nullable = false)
    private String country;

    @Column(nullable = false)
    private LocalDateTime loginTime;

    @Column(nullable = false)
    private boolean success;

    public Login() {
        // Constructor requerido por JPA
    }

    public Login(Long id, User user, Device device, String country, LocalDateTime loginTime, boolean success) {
        this.id = id;
        this.user = user;
        this.device = device;
        this.country = country;
        this.loginTime = loginTime;
        this.success = success;
    }

    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }


    public Device getDevice() {
        return device;
    }

    public void setDevice(Device device) {
        this.device = device;
    }

    public String getCountry() {
        return country;
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

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}

