package com.pei.dto;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
public class RecurringBeneficiaryAlert {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private Long idBeneficiary;

    @CreationTimestamp
    private LocalDateTime dateTime;

    @Column
    private String message;


    public RecurringBeneficiaryAlert() { }

    public RecurringBeneficiaryAlert(Long idBeneficiary, LocalDateTime dateTime, String message) {
        this.idBeneficiary = idBeneficiary;
        this.dateTime = dateTime;
        this.message = message;
    }

    public Long getId() {
        return id;
    }

    public Long getIdBeneficiary() {
        return idBeneficiary;
    }

    public void setIdBeneficiary(Long idBeneficiary) {
        this.idBeneficiary = idBeneficiary;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
