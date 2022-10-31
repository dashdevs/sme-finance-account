package com.sme.finance.account.controller;

import com.sme.finance.account.AccountService;
import com.sme.finance.account.rest.api.AccountsApi;
import com.sme.finance.account.rest.model.AccountExchange;
import com.sme.finance.account.rest.model.CheckAccountStatusResponse;
import com.sme.finance.account.rest.model.UpdateAccountBalanceRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AccountController implements AccountsApi {

    private final AccountService accountService;

    @Override
    public ResponseEntity<AccountExchange> checkAccountBalanceById(final Long id) {
        return ResponseEntity.ok(
                accountService.checkAccountBalance(id)
        );
    }

    @Override
    public ResponseEntity<CheckAccountStatusResponse> checkAccountStatusById(final Long id) {
        return ResponseEntity.ok(
                accountService.checkAccountStatus(id)
        );
    }

    @Override
    public ResponseEntity<AccountExchange> updateAccountBalance(final UpdateAccountBalanceRequest updateAccountBalanceRequest) {
        return ResponseEntity.ok(
                accountService.updateAccountBalance(updateAccountBalanceRequest)
        );
    }
}
