package com.sme.finance.account.repository;

import com.sme.finance.account.domain.AccountTransactionLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountTransactionLogRepository extends JpaRepository<AccountTransactionLogEntity, Long> {
}
