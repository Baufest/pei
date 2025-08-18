package com.pei.repository;

import com.pei.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    String findClientTypeById(Long userId);
}
