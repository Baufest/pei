package com.pei.domain;

import jakarta.persistence.*;

@Entity
@Table(name="approvals")
public class Approval {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String approverName;

    @Column(nullable = false)
    private boolean approved;

    @ManyToOne
    @JoinColumn(name = "transaction_id", nullable = false)
    private Transaction transaction;

    public Approval(){}

    public Approval(Long id, String approverName, boolean approved, Transaction transaction) {
        this.id = id;
        this.approverName = approverName;
        this.approved = approved;
        this.transaction = transaction;
    }

    public Long getId() {
        return id;
    }

    public boolean isApproved() {
        return approved;
    }

    public void setApproved(boolean approved) {
        this.approved = approved;
    }

    public String getApproverName() {
        return approverName;
    }

    public void setApproverName(String approverName) {
        this.approverName = approverName;
    }

    public Transaction getTransaction() {
        return transaction;
    }

    public void setTransaction(Transaction transaction) {
        this.transaction = transaction;
    }
}
