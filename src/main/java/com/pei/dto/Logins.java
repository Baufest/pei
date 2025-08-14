package com.pei.dto;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public record Logins(
        @Id 
        Long id,
        Long userId,
        String deviceID,
        String country,
        LocalDateTime loginTime,
        boolean success) {

    public String getCountry() {
        return country;
    }

}
