package com.sme.finance.account.repository;

import com.sme.finance.account.domain.AccountEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
@SuppressWarnings("unused") // used by other modules
public interface AccountRepository extends CrudRepository<AccountEntity, Long> {
}
