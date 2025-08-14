package com.pei.config.DataDummyLoader;

import com.pei.domain.Account;
import com.pei.domain.User;
import com.pei.repository.AccountRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AccountDataLoader {

    private final AccountRepository accountRepository;

    public AccountDataLoader(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public List<Account> insertAccounts(List<User> users) {
        if (accountRepository.count() > 0) {
            return accountRepository.findAll();
        }

        Account a1 = new Account();
        a1.setType("SAVINGS");
        a1.setOwner(users.get(0));

        Account a2 = new Account();
        a2.setType("CHECKING");
        a2.setOwner(users.get(1));

        return accountRepository.saveAll(List.of(a1, a2));
    }
}
