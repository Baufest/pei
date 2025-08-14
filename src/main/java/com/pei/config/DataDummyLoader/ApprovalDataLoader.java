package com.pei.config.DataDummyLoader;

import com.pei.domain.Approval;
import com.pei.domain.Transaction;
import com.pei.repository.ApprovalRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ApprovalDataLoader {

    private final ApprovalRepository approvalRepository;

    public ApprovalDataLoader(ApprovalRepository approvalRepository) {
        this.approvalRepository = approvalRepository;
    }

    public void insertApprovals(List<Transaction> transactions) {
        if (approvalRepository.count() > 0) return;

        Approval ap1 = new Approval();
        ap1.setApproverName("Admin1");
        ap1.setApproved(true);
        ap1.setTransaction(transactions.get(0));

        Approval ap2 = new Approval();
        ap2.setApproverName("Admin2");
        ap2.setApproved(false);
        ap2.setTransaction(transactions.get(1));

        approvalRepository.saveAll(List.of(ap1, ap2));
    }
}
