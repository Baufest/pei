package com.pei.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.pei.domain.AccountParam;

@Repository
public interface AccountParamsRepository extends JpaRepository<AccountParam, Long> {
        Optional<AccountParam> findByKeyNameAndActiveTrue(String keyName);

}
