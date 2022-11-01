package com.sme.finance.account.mapper;

import com.sme.finance.account.domain.AccountEntity;
import com.sme.finance.account.domain.AccountTransactionLogEntity;
import com.sme.finance.account.domain.AccountTransactionLogEntityType;
import com.sme.finance.account.rest.model.UpdateAccountBalanceRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.Objects;

@Mapper(componentModel = "spring")
public interface AccountTransactionLogMapper {

    @Mapping(target = "id", ignore = true)
    default AccountTransactionLogEntity toAccountTransactionLogEntity(final UpdateAccountBalanceRequest request, final AccountEntity accountEntity) {
        return new AccountTransactionLogEntity()
            .setCurrency(request.getCurrency())
            .setAmount(request.getAmount())
            .setType(toAccountTransactionLogEntityType(request))
            .setAccount(accountEntity);
    }

    default AccountTransactionLogEntityType toAccountTransactionLogEntityType(final UpdateAccountBalanceRequest request) {
        Objects.requireNonNull(request.getOperationType(), "UpdateAccountBalanceRequest#OperationType is required");

        return AccountTransactionLogEntityType.from(request.getOperationType().getValue());
    }
}
