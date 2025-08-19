package com.pei.repository;

import java.time.LocalDateTime;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.pei.domain.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    String findClientTypeById(Long userId);

    @Query("SELECT CASE WHEN u.creationDate >= :dateLimit THEN true ELSE false END " +
           "FROM User u WHERE u.id = :userId")
    boolean isANewUser(@Param("userId") Long userId,
                       @Param("dateLimit") LocalDateTime dateLimit);
}
