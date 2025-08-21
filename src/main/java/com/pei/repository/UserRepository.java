package com.pei.repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.pei.domain.User.User;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    String findClientTypeById(Long userId);

}
