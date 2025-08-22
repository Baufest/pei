package com.pei.repository;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.pei.domain.RiskCountry;

//Se usa cuando implementemos BD
@Repository
public interface RiskCountryRepository extends JpaRepository<RiskCountry, Long> {
    List<RiskCountry> findByActiveTrue();
}
