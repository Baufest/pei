package com.pei.config.DataDummyLoader;

import com.pei.domain.Account;
import com.pei.domain.Transaction;
import com.pei.domain.User;
import com.pei.repository.TransactionRepository;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Component
public class TransactionDataLoader {

    final TransactionRepository transactionRepository;

    public TransactionDataLoader(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public List<Transaction> findAll() {
        return transactionRepository.findAll();
    }

    public void insertTransactions(List<User> users, List<Account> accounts) {
        if (transactionRepository.count() > 0) {
            return;
        }

        Transaction t1 = new Transaction();
        t1.setUser(users.get(0));
        t1.setAmount(BigDecimal.valueOf(500));
        t1.setDate(LocalDateTime.now().minusDays(1));
        t1.setSourceAccount(accounts.get(0));
        t1.setDestinationAccount(accounts.get(1));

        Transaction t2 = new Transaction();
        t2.setUser(users.get(1));
        t2.setAmount(BigDecimal.valueOf(1200));
        t2.setDate(LocalDateTime.now().minusHours(5));
        t2.setSourceAccount(accounts.get(1));
        t2.setDestinationAccount(accounts.get(0));

        transactionRepository.saveAll(List.of(t1, t2));
    }
}
