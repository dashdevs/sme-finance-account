package com.sme.finance.account.controller;

import com.sme.finance.account.AccountService;
import com.sme.finance.account.rest.model.AccountExchange;
import com.sme.finance.account.rest.model.CheckAccountStatusResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountControllerTest {

    @Mock
    private AccountService accountService;

    @InjectMocks
    private AccountController accountController;

    @Test
    void shouldCheckAccountBalanceById() {
        // Given
        AccountExchange body = mock(AccountExchange.class);
        when(accountService.checkAccountBalance(1L)).thenReturn(body);

        // When
        ResponseEntity<AccountExchange> response = accountController.checkAccountBalanceById(1L);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(body, response.getBody());
    }

    @Test
    void shouldCheckAccountStatusById() {
        // Given
        CheckAccountStatusResponse body = mock(CheckAccountStatusResponse.class);
        when(accountService.checkAccountStatus(1L)).thenReturn(body);

        // When
        ResponseEntity<CheckAccountStatusResponse> response = accountController.checkAccountStatusById(1L);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(body, response.getBody());
    }

    @Test
    void shouldUpdateAccountBalance() {
        // Given
        AccountExchange body = mock(AccountExchange.class);
        when(accountService.updateAccountBalance(any())).thenReturn(body);

        // When
        ResponseEntity<AccountExchange> response = accountController.updateAccountBalance(null);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(body, response.getBody());
    }
}
