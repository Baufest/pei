package com.pei.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pei.domain.User;

public interface UserRepository extends JpaRepository<User, Long>{
    
}
