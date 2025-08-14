package com.pei.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.pei.dto.Logins;

public interface LoginsRepository extends JpaRepository<Logins, Long> {

    @Query("SELECT l FROM Logins l WHERE l.userId = :userId AND l.loginTime >= :timeLimit")
    List<Logins> findRecentLogins(@Param("userId") Long userId, @Param("timeLimit") LocalDateTime timeLimit);

    @Query("SELECT l FROM Logins l " +
            "WHERE l.userId = :userId " +
            "AND l.country = :country " +
            "AND l.deviceID = :deviceID " +
            "AND l.success = :success")
    List<Logins> findLoginsByUserAndCountryAndDevice(
            @Param("userId") Long userId,
            @Param("country") String country,
            @Param("deviceID") String deviceID,
            @Param("success") boolean success);

}