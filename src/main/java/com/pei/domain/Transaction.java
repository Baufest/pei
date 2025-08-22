package com.pei.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.pei.domain.Account.Account;
import com.pei.domain.User.User;
import com.pei.dto.TransactionDTO;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "user_transaction")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private User user;

    @Column(nullable = false)
    private BigDecimal amount;

    @Column(nullable = false)
    private LocalDateTime date;

    @Column
    private String currency; // Moneda

    @Column(name = "codigo_coelsa", length = 22)
    private String codCoelsa; // Código Coelsa alfanumérico de longitud de 22 para cada transacción

    @ManyToOne
    @JoinColumn(name = "source_account", referencedColumnName = "id", nullable = false)
    private Account sourceAccount;

    @ManyToOne
    @JoinColumn(name = "destination_account", referencedColumnName = "id", nullable = false)
    private Account destinationAccount;

    @OneToMany(mappedBy = "transaction")
    private List<Approval> approvalList = new ArrayList<>();

    @Column(nullable = false)
    private TransactionStatus status;

    public enum TransactionStatus {
        REQUIERE_APROBACION,
        APROBADA,
        RECHAZADA
    }
    public Transaction() {
    }

    public Transaction(BigDecimal amount) {
        this.amount = amount;
    }

    public Transaction(User user, BigDecimal amount, LocalDateTime date, Account sourceAccount,
            Account destinationAccount) {
        this.user = user;
        this.amount = amount;
        this.date = date;
        this.sourceAccount = sourceAccount;
        this.destinationAccount = destinationAccount;

    }
    public Transaction(Long id, BigDecimal amount, LocalDateTime date, Account sourceAccount,
            Account destinationAccount, List<Approval> approvalList) {
        this.id = id;
        this.amount = amount;
        this.date = date;
        this.sourceAccount = sourceAccount;
        this.destinationAccount = destinationAccount;
        this.approvalList = approvalList;
    }

    public TransactionStatus getStatus() {
        return status;
    }
 
    public void setStatus(TransactionStatus status) {
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Account getSourceAccount() {
        return sourceAccount;
    }

    public void setSourceAccount(Account sourceAccount) {
        this.sourceAccount = sourceAccount;
    }

    public Account getDestinationAccount() {
        return destinationAccount;
    }

    public void setDestinationAccount(Account destinationAccount) {
        this.destinationAccount = destinationAccount;
    }

    public List<Approval> getApprovalList() {
        return approvalList;
    }

    public void setApprovalList(List<Approval> approvalList) {
        this.approvalList = approvalList;
    }
    public boolean isInternational() {
        if (sourceAccount == null || destinationAccount == null) {
            return false;
        }
        String sourceCountry = sourceAccount.getCountry();
        String destinationCountry = destinationAccount.getCountry();
        if (sourceCountry == null || destinationCountry == null) {
            throw new IllegalArgumentException("sourceCountry o destinationCountry no pueden ser null");
        }
        return !sourceCountry.equalsIgnoreCase(destinationCountry);
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public TransactionDTO transactionToDto() {
        return new TransactionDTO(
            this.id,
            this.codCoelsa != null ? this.codCoelsa : "",
            this.amount,
            this.destinationAccount != null ? this.getCurrency() : null,
            this.destinationAccount != null ? this.destinationAccount.getId() : null,
            this.date != null ? this.date : LocalDateTime.now()
        );
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass())
            return false;
        Transaction that = (Transaction) o;
        return Objects.equals(id, that.id) && Objects.equals(user, that.user) && Objects.equals(amount, that.amount) && Objects.equals(date, that.date) && Objects.equals(currency, that.currency) && Objects.equals(sourceAccount, that.sourceAccount) && Objects.equals(destinationAccount, that.destinationAccount) && Objects.equals(approvalList, that.approvalList);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, user, amount, date, currency, sourceAccount, destinationAccount, approvalList);
    }
}
