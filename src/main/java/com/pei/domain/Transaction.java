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

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public LocalDateTime getDateHour() {
        return dateHour;
    }

    public void setDateHour(LocalDateTime dateHour) {
        this.dateHour = dateHour;
    }

    public String getAccountOrigin() {
        return accountOrigin;
    }

    public void setAccountOrigin(String accountOrigin) {
        this.accountOrigin = accountOrigin;
    }

    public String getAccountDestiny() {
        return accountDestiny;
    }

    public void setAccountDestiny(String accountDestiny) {
        this.accountDestiny = accountDestiny;
    }

    public List<Approval> getApprovalList() {
        return approvalList;
    }

    public void setApprovalList(List<Approval> approvalList) {
        this.approvalList = approvalList;
    }
}

