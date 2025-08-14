package com.pei.config.DataDummyLoader;

import com.pei.dto.Logins;
import com.pei.repository.LoginsRepository;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class LoginsDataLoader {

    private final LoginsRepository loginsRepository;

    public LoginsDataLoader(LoginsRepository loginsRepository) {
        this.loginsRepository = loginsRepository;
    }

    public void insertLogins(Long userId1, Long userId2) {
        if (loginsRepository.count() > 0) return;

        Logins l1 = new Logins(userId1, "Argentina", LocalDateTime.now().minusDays(1));
        Logins l2 = new Logins(userId1, "Chile", LocalDateTime.now().minusHours(5));
        Logins l3 = new Logins(userId2, "Argentina", LocalDateTime.now().minusHours(3));

        loginsRepository.saveAll(List.of(l1, l2, l3));
    }
}
