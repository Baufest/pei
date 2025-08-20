package com.pei.repository;

import com.pei.dto.RecurringBeneficiaryAlert;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RecurringBeneficiaryAlertRepository extends JpaRepository<RecurringBeneficiaryAlert, Long> {
}
