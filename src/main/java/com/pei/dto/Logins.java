package com.pei.dto;

import java.time.LocalDateTime;

public record Logins(    
    Long userId, 

    String country,
    
    LocalDateTime loginTime) {

        public String getCountry() {
            return country;}
    } 
