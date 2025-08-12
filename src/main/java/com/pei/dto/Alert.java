package com.pei.dto;

public record Alert(Long userId, String description) {

    public Long getUserId() {
        return userId;
    }

    public Object getDescription() {
        return description;
    }
}