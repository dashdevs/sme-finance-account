package com.sme.finance.account.mapper;

import com.sme.finance.account.domain.AccountEntity;
import com.sme.finance.account.domain.AccountEntityStatus;
import com.sme.finance.account.domain.AccountTransactionLogEntity;
import com.sme.finance.account.domain.AccountTransactionLogEntityType;
import com.sme.finance.account.rest.model.BalanceOperationType;
import com.sme.finance.account.rest.model.UpdateAccountBalanceRequest;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class AccountTransactionLogMapperTest {

    private final AccountTransactionLogMapper mapper = new AccountTransactionLogMapperImpl();

    @Test
    void shouldMapToAccountTransactionLogEntity() {
        // Given
        AccountEntity entity = new AccountEntity()
            .setId(1L)
            .setAccountNumber("1234")
            .setBalance(BigDecimal.TEN)
            .setCurrency("978")
            .setStatus(AccountEntityStatus.OPEN);

        UpdateAccountBalanceRequest request =
            new UpdateAccountBalanceRequest()
                .operationType(BalanceOperationType.DEBIT)
                .amount(BigDecimal.ONE)
                .currency("978");

        // When
        AccountTransactionLogEntity log = mapper.toAccountTransactionLogEntity(request, entity);

        // Then
        assertNull(log.getId());
        assertEquals("978", log.getCurrency());
        assertEquals(BigDecimal.ONE, log.getAmount());
        assertEquals(AccountTransactionLogEntityType.DEBIT, log.getType());
    }
}
