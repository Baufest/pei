package com.pei.config.DataDummyLoader;

import com.pei.domain.Account;
import com.pei.domain.Transaction;
import com.pei.domain.User;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Profile("dev")
public class DummyDataLoader implements CommandLineRunner {

    private final UserDataLoader userDataLoader;
    private final AccountDataLoader accountDataLoader;
    private final TransactionDataLoader transactionDataLoader;
    private final ApprovalDataLoader approvalDataLoader;
    private final LoginsDataLoader loginsDataLoader;
    private final PurchaseDataLoader purchaseDataLoader;
    private final ChargebackDataLoader chargebackDataLoader;
    private final UserEventDataLoader userEventDataLoader;

    public DummyDataLoader(UserDataLoader userDataLoader,
                           AccountDataLoader accountDataLoader,
                           TransactionDataLoader transactionDataLoader,
                           ApprovalDataLoader approvalDataLoader,
                           LoginsDataLoader loginsDataLoader,
                           PurchaseDataLoader purchaseDataLoader,
                           ChargebackDataLoader chargebackDataLoader,
                           UserEventDataLoader userEventDataLoader) {
        this.userDataLoader = userDataLoader;
        this.accountDataLoader = accountDataLoader;
        this.transactionDataLoader = transactionDataLoader;
        this.approvalDataLoader = approvalDataLoader;
        this.loginsDataLoader = loginsDataLoader;
        this.purchaseDataLoader = purchaseDataLoader;
        this.chargebackDataLoader = chargebackDataLoader;
        this.userEventDataLoader = userEventDataLoader;
    }

    @Override
    public void run(String... args) {
        List<User> users = userDataLoader.insertUsers();
        List<Account> accounts = accountDataLoader.insertAccounts(users);

        transactionDataLoader.insertTransactions(users, accounts);
        List<Transaction> transactions = transactionDataLoader.findAll();

        approvalDataLoader.insertApprovals(transactions);
        loginsDataLoader.insertLogins(users.get(0).getId(), users.get(1).getId());
        purchaseDataLoader.insertPurchases(users);
        chargebackDataLoader.insertChargebacks(users);
        userEventDataLoader.insertUserEvents(users);
    }
}
