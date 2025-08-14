package com.pei.config.DataDummyLoader;

import com.pei.domain.User;
import com.pei.domain.Chargeback;
import com.pei.repository.ChargebackRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ChargebackDataLoader {

    private final ChargebackRepository chargebackRepository;

    public ChargebackDataLoader(ChargebackRepository chargebackRepository) {
        this.chargebackRepository = chargebackRepository;
    }

    public void insertChargebacks(List<User> users) {
        if (chargebackRepository.count() > 0) return;

        Chargeback c1 = new Chargeback();
        c1.setUser(users.get(0));

        Chargeback c2 = new Chargeback();
        c2.setUser(users.get(1));

        chargebackRepository.saveAll(List.of(c1, c2));
    }
}

