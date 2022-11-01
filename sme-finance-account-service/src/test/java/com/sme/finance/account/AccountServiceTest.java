package com.sme.finance.account;

import com.sme.finance.account.domain.AccountEntity;
import com.sme.finance.account.domain.AccountEntityStatus;
import com.sme.finance.account.error.InsufficientFundAlertException;
import com.sme.finance.account.error.UnsupportedCurrencyAlertException;
import com.sme.finance.account.mapper.AccountMapper;
import com.sme.finance.account.mapper.AccountTransactionLogMapper;
import com.sme.finance.account.repository.AccountRepository;
import com.sme.finance.account.repository.AccountTransactionLogRepository;
import com.sme.finance.account.rest.model.AccountExchange;
import com.sme.finance.account.rest.model.BalanceOperationType;
import com.sme.finance.account.rest.model.CheckAccountStatusResponse;
import com.sme.finance.account.rest.model.UpdateAccountBalanceRequest;
import com.sme.finance.core.error.exception.BadRequestAlertException;
import com.sme.finance.core.error.exception.NotFoundAlertException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock
    private AccountMapper accountMapper;
    @Mock
    private AccountRepository accountRepository;
    @Mock
    private AccountTransactionLogMapper accountTransactionLogMapper;
    @Mock
    private AccountTransactionLogRepository accountTransactionLogRepository;

    @InjectMocks
    private AccountService accountService;

    @Test
    void shouldCheckAccountStatus() {
        // Given
        AccountEntity entity = mock(AccountEntity.class);
        when(accountRepository.findById(1L)).thenReturn(Optional.of(entity));

        CheckAccountStatusResponse response = mock(CheckAccountStatusResponse.class);
        when(accountMapper.toCheckAccountStatusResponse(entity)).thenReturn(response);

        // When
        CheckAccountStatusResponse actualResponse = accountService.checkAccountStatus(1L);

        // Then
        assertEquals(response, actualResponse);
    }

    @Test
    void shouldThrowWhenAccountNotFoundWhileStatusCheck() {
        // Given
        when(accountRepository.findById(1L)).thenReturn(Optional.empty());

        // When
        NotFoundAlertException exception = assertThrows(NotFoundAlertException.class, () -> accountService.checkAccountStatus(1L));

        // Then
        assertEquals("Not found: Account=1 doesn't exist", exception.getLocalizedMessage());
    }

    @Test
    void shouldCheckAccountBalance() {
        // Given
        AccountEntity entity = mock(AccountEntity.class);
        when(accountRepository.findById(1L)).thenReturn(Optional.of(entity));

        AccountExchange response = mock(AccountExchange.class);
        when(accountMapper.toAccountExchange(entity)).thenReturn(response);

        // When
        AccountExchange actualResponse = accountService.checkAccountBalance(1L);

        // Then
        assertEquals(response, actualResponse);
    }

    @Test
    void shouldThrowWhenAccountNotFoundWhileBalanceCheck() {
        // Given
        when(accountRepository.findById(1L)).thenReturn(Optional.empty());

        // When
        NotFoundAlertException exception = assertThrows(NotFoundAlertException.class, () -> accountService.checkAccountBalance(1L));

        // Then
        assertEquals("Not found: Account=1 doesn't exist", exception.getLocalizedMessage());
    }

    @Test
    void shouldThrowWhenAccountNotFoundWhileBalanceUpdate() {
        // Given
        when(accountRepository.findById(1L)).thenReturn(Optional.empty());
        UpdateAccountBalanceRequest request = new UpdateAccountBalanceRequest().id(1L);

        // When
        NotFoundAlertException exception = assertThrows(NotFoundAlertException.class, () -> accountService.updateAccountBalance(request));

        // Then
        assertEquals("Not found: Account=1 doesn't exist", exception.getLocalizedMessage());
    }

    @Test
    void shouldThrowWhenAccountClosedWhileBalanceUpdate() {
        // Given
        AccountEntity entity = new AccountEntity().setStatus(AccountEntityStatus.CLOSED);
        when(accountRepository.findById(1L)).thenReturn(Optional.of(entity));

        UpdateAccountBalanceRequest request = new UpdateAccountBalanceRequest().id(1L);

        // When
        BadRequestAlertException exception = assertThrows(BadRequestAlertException.class, () -> accountService.updateAccountBalance(request));

        // Then
        assertEquals("Invalid request: Invalid account status={CLOSED}", exception.getLocalizedMessage());
    }

    @Test
    void shouldThrowWhenCurrencyMismatchWhileBalanceUpdate() {
        // Given
        AccountEntity entity = new AccountEntity().setStatus(AccountEntityStatus.OPEN).setCurrency("978");
        when(accountRepository.findById(1L)).thenReturn(Optional.of(entity));

        UpdateAccountBalanceRequest request = new UpdateAccountBalanceRequest().id(1L).currency("314");

        // When
        UnsupportedCurrencyAlertException exception = assertThrows(UnsupportedCurrencyAlertException.class, () -> accountService.updateAccountBalance(request));

        // Then
        assertEquals("Unsupported currency: Account doesn't support currency={314}", exception.getLocalizedMessage());
    }

    @Test
    void shouldThrowWhenFundInsufficientWhileBalanceUpdate() {
        // Given
        AccountEntity entity = new AccountEntity()
            .setStatus(AccountEntityStatus.OPEN)
            .setCurrency("978")
            .setBalance(BigDecimal.ZERO);
        when(accountRepository.findById(1L)).thenReturn(Optional.of(entity));

        UpdateAccountBalanceRequest request = new UpdateAccountBalanceRequest().id(1L)
            .currency("978")
            .operationType(BalanceOperationType.DEBIT)
            .amount(BigDecimal.TEN);

        // When
        InsufficientFundAlertException exception = assertThrows(InsufficientFundAlertException.class, () -> accountService.updateAccountBalance(request));

        // Then
        assertEquals("Insufficient funds: Account balance is less than 10", exception.getLocalizedMessage());
    }

    @Test
    void shouldDebitAccount() {
        // Given
        AccountEntity entity = new AccountEntity()
            .setStatus(AccountEntityStatus.OPEN)
            .setCurrency("978")
            .setBalance(BigDecimal.TEN);
        when(accountRepository.findById(1L)).thenReturn(Optional.of(entity));

        UpdateAccountBalanceRequest request = new UpdateAccountBalanceRequest().id(1L)
            .currency("978")
            .operationType(BalanceOperationType.DEBIT)
            .amount(BigDecimal.TEN);

        // When
        AccountExchange ignored = accountService.updateAccountBalance(request);

        // Then
        assertEquals(BigDecimal.ZERO, entity.getBalance());
        verify(accountRepository).save(entity);
    }

    @Test
    void shouldCreditAccount() {
        // Given
        AccountEntity entity = new AccountEntity()
            .setStatus(AccountEntityStatus.OPEN)
            .setCurrency("978")
            .setBalance(BigDecimal.TEN);
        when(accountRepository.findById(1L)).thenReturn(Optional.of(entity));

        UpdateAccountBalanceRequest request = new UpdateAccountBalanceRequest().id(1L)
            .currency("978")
            .operationType(BalanceOperationType.CREDIT)
            .amount(BigDecimal.TEN);

        // When
        AccountExchange ignored = accountService.updateAccountBalance(request);

        // Then
        assertEquals(BigDecimal.valueOf(20), entity.getBalance());
        verify(accountRepository).save(entity);
    }

}
