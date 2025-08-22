package com.pei.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.pei.domain.Login;

public interface LoginRepository extends JpaRepository<Login, Long> {

    @Query("SELECT l FROM Login l WHERE l.user.id = :userId AND l.loginTime >= :timeLimit")
    List<Login> findRecentLogin(@Param("userId") Long userId, @Param("timeLimit") LocalDateTime timeLimit);

    @Query("SELECT l FROM Login l " +
        "WHERE l.user.id = :userId " +
        "AND l.country = :country " +
        "AND l.device.deviceId = :deviceID " +
        "AND l.success = :success")
    List<Login> findLoginByUserAndCountryAndDevice(
        @Param("userId") Long userId,
        @Param("country") String country,
        @Param("deviceId") Long deviceID,
        @Param("success") boolean success);


}
