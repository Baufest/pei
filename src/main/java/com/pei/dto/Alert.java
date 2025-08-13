package com.pei.dto;

public record Alert(Long userId, String description) {

    //Chequear si los getters son necesarios, en teoria no pero no funcionan los test si los borro
    public Long getUserId() {
       return userId;}

    public Object getDescription() {
       return description;}
}
