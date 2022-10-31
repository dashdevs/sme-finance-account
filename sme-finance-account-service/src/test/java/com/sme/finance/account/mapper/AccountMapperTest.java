package com.sme.finance.account.mapper;

import com.sme.finance.account.domain.AccountEntity;
import com.sme.finance.account.domain.AccountEntityStatus;
import com.sme.finance.account.rest.model.AccountExchange;
import com.sme.finance.account.rest.model.AccountStatus;
import com.sme.finance.account.rest.model.CheckAccountStatusResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class AccountMapperTest {

    private final AccountMapper accountMapper = new AccountMapperImpl();

    @Test
    void shouldMapToCheckAccountStatusResponse() {
        // Given
        AccountEntity accountEntity = getAccountEntity();

        // When
        CheckAccountStatusResponse checkAccountStatusResponse = accountMapper.toCheckAccountStatusResponse(accountEntity);

        // Then
        assertEquals(accountEntity.getId(), checkAccountStatusResponse.getId());
        assertEquals(accountEntity.getStatus().name(), checkAccountStatusResponse.getStatus().name());
    }

    @Test
    void shouldMapToAccountExchange() {
        // Given
        AccountEntity accountEntity = getAccountEntity();

        // When
        AccountExchange accountExchange = accountMapper.toAccountExchange(accountEntity);

        // Then
        assertEquals(accountEntity.getId(), accountExchange.getId());
        assertEquals(accountEntity.getBalance(), accountExchange.getBalance());
        assertEquals(accountEntity.getCurrency(), accountExchange.getCurrency());
        assertEquals(accountEntity.getAccountNumber(), accountExchange.getAccountNumber());
        assertEquals(accountEntity.getStatus().name(), accountExchange.getStatus().name());
    }

    private AccountEntity getAccountEntity() {
        return new AccountEntity()
            .setId(1L)
            .setAccountNumber("1234")
            .setBalance(BigDecimal.TEN)
            .setCurrency("978")
            .setStatus(AccountEntityStatus.OPEN);
    }
}
