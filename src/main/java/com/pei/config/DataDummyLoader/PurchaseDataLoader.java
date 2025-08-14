package com.pei.config.DataDummyLoader;

import com.pei.domain.User;
import com.pei.domain.Purchase;
import com.pei.repository.PurchaseRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PurchaseDataLoader {

    private final PurchaseRepository purchaseRepository;

    public PurchaseDataLoader(PurchaseRepository purchaseRepository) {
        this.purchaseRepository = purchaseRepository;
    }

    public void insertPurchases(List<User> users) {
        if (purchaseRepository.count() > 0) return;

        Purchase p1 = new Purchase();
        p1.setUser(users.get(0));

        Purchase p2 = new Purchase();
        p2.setUser(users.get(1));

        purchaseRepository.saveAll(List.of(p1, p2));
    }
}
