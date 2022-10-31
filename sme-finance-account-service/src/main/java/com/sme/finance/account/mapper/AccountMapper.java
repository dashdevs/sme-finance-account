package com.sme.finance.account.mapper;

import com.sme.finance.account.domain.AccountEntity;
import com.sme.finance.account.rest.model.AccountExchange;
import com.sme.finance.account.rest.model.AccountStatus;
import com.sme.finance.account.rest.model.CheckAccountStatusResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AccountMapper {

    @Mapping(target = "status", expression = "java(toAccountStatus(entity))")
    CheckAccountStatusResponse toCheckAccountStatusResponse(final AccountEntity entity);

    @Mapping(target = "status", expression = "java(toAccountStatus(entity))")
    AccountExchange toAccountExchange(AccountEntity entity);

    default AccountStatus toAccountStatus(final AccountEntity entity) {
        return AccountStatus.fromValue(entity.getStatus().name());
    }
}
