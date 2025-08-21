package com.pei.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.pei.domain.User.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    String findClientTypeById(Long userId);
}
