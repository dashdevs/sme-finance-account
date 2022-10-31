package com.sme.finance.account;

import com.sme.finance.account.domain.AccountEntity;
import com.sme.finance.account.domain.AccountEntityStatus;
import com.sme.finance.account.error.InsufficientFundAlertException;
import com.sme.finance.account.error.UnsupportedCurrencyAlertException;
import com.sme.finance.account.mapper.AccountMapper;
import com.sme.finance.account.repository.AccountRepository;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock
    private AccountMapper accountMapper;
    @Mock
    private AccountRepository accountRepository;

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
        assertEquals("1", exception.getErrorKey());
        assertEquals("Account", exception.getEntityName());
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
        assertEquals("1", exception.getErrorKey());
        assertEquals("Account", exception.getEntityName());
    }

    @Test
    void shouldThrowWhenAccountNotFoundWhileBalanceUpdate() {
        // Given
        when(accountRepository.findById(1L)).thenReturn(Optional.empty());
        UpdateAccountBalanceRequest request = new UpdateAccountBalanceRequest().id(1L);

        // When
        NotFoundAlertException exception = assertThrows(NotFoundAlertException.class, () -> accountService.updateAccountBalance(request));

        // Then
        assertEquals("1", exception.getErrorKey());
        assertEquals("Account", exception.getEntityName());
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
        assertEquals("1", exception.getErrorKey());
        assertEquals("Account must be OPEN", exception.getEntityName());
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
        assertEquals("314", exception.getErrorKey());
        assertEquals("Account", exception.getEntityName());
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
            .balance(BigDecimal.TEN);

        // When
        InsufficientFundAlertException exception = assertThrows(InsufficientFundAlertException.class, () -> accountService.updateAccountBalance(request));

        // Then
        assertEquals("1", exception.getErrorKey());
        assertEquals("Account", exception.getEntityName());
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
            .balance(BigDecimal.TEN);

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
            .balance(BigDecimal.TEN);

        // When
        AccountExchange ignored = accountService.updateAccountBalance(request);

        // Then
        assertEquals(BigDecimal.valueOf(20), entity.getBalance());
        verify(accountRepository).save(entity);
    }

}
