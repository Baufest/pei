package com.pei.config.DataDummyLoader;

import com.pei.domain.User;
import com.pei.repository.UserRepository;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Component
public class UserDataLoader {

    private final UserRepository userRepository;

    public UserDataLoader(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> insertUsers() {
        if (userRepository.count() > 0) {
            return userRepository.findAll();
        }

        User u1 = new User();
        u1.setName("Juan Pérez");
        u1.setEmail("juan@example.com");
        u1.setPhoneNumber("111-1111");
        u1.setRisk("inversionista");
        u1.setProfile("irrecuperable");
        u1.setAverageMonthlySpending(BigDecimal.valueOf(1500));
        u1.setCreationDate(LocalDate.now());

        User u2 = new User();
        u2.setName("María López");
        u2.setEmail("maria@example.com");
        u2.setPhoneNumber("222-2222");
        u2.setRisk("medio");
        u2.setProfile("ahorrista");
        u2.setAverageMonthlySpending(BigDecimal.valueOf(3000));
        u2.setCreationDate(LocalDate.now());

        return userRepository.saveAll(List.of(u1, u2));
    }
}
