package com.pei.domain;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Entity
@Table(name="transaction")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private BigDecimal amount;

    @Column(nullable = false)
    private LocalDateTime dateHour;

    @Column(nullable = false)
    private String accountOrigin;

    @Column(nullable = false)
    private String accountDestiny;

    @OneToMany(mappedBy = "transaction")
    private List<Approval> approvalList = new ArrayList<>();

    public Transaction() {
    }

    public Transaction(Long id, BigDecimal amount, LocalDateTime dateHour, String accountOrigin, String accountDestiny, List<Approval> approvalList) {
        this.id = id;
        this.amount = amount;
        this.dateHour = dateHour;
        this.accountOrigin = accountOrigin;
        this.accountDestiny = accountDestiny;
        this.approvalList = approvalList;
    }

    public Long getId() {
        return id;
    }

    public BigDecimal getMonto() {
        return amount;
    }

    public void setMonto(BigDecimal monto) {
        this.amount = monto;
    }

    public LocalDateTime getFechaHora() {
        return dateHour;
    }

    public void setFechaHora(LocalDateTime fechaHora) {
        this.dateHour = fechaHora;
    }

    public String getCuentaOrigen() {
        return accountOrigin;
    }

    public void setCuentaOrigen(String cuentaOrigen) {
        this.accountOrigin = cuentaOrigen;
    }

    public String getCuentaDestino() {
        return accountDestiny;
    }

    public void setCuentaDestino(String cuentaDestino) {
        this.accountDestiny = cuentaDestino;
    }

    public List<Approval> getApprovalList() {
        return approvalList;
    }

    public void setApprovalList(List<Approval> approvalList) {
        this.approvalList = approvalList;
    }
}

