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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Objects;

@Slf4j
@Component
@RequiredArgsConstructor
public class AccountService {

    private static final String ACCOUNT_ERROR_PREFIX = "Account";
    private static final String NO_ACCOUNT_FOUND_MESSAGE = "No account found for given id={}";

    private final AccountMapper accountMapper;
    private final AccountRepository accountRepository;
    private final AccountTransactionLogMapper accountTransactionLogMapper;
    private final AccountTransactionLogRepository accountTransactionLogRepository;

    public CheckAccountStatusResponse checkAccountStatus(final Long id) {
        Objects.requireNonNull(id, "id is required");

        accountRepository.save(
            new AccountEntity()
                .setBalance(BigDecimal.valueOf(10000))
                .setCurrency("978")
                .setAccountNumber("1112")
                .setStatus(AccountEntityStatus.OPEN)
        );

        return accountRepository.findById(id)
            .map(accountMapper::toCheckAccountStatusResponse)
            .orElseThrow(() -> {
                log.error(NO_ACCOUNT_FOUND_MESSAGE, id);
                return new NotFoundAlertException(ACCOUNT_ERROR_PREFIX, id);
            });
    }

    public AccountExchange checkAccountBalance(final Long id) {
        Objects.requireNonNull(id, "id is required");

        return accountRepository.findById(id)
            .map(accountMapper::toAccountExchange)
            .orElseThrow(() -> {
                log.error(NO_ACCOUNT_FOUND_MESSAGE, id);
                return new NotFoundAlertException(ACCOUNT_ERROR_PREFIX, id);
            });
    }

    @Transactional
    public AccountExchange updateAccountBalance(final UpdateAccountBalanceRequest request) {
        final AccountEntity entity = accountRepository.findById(request.getId())
            .orElseThrow(() -> {
                log.error(NO_ACCOUNT_FOUND_MESSAGE, request.getId());
                return new NotFoundAlertException(ACCOUNT_ERROR_PREFIX, request.getId());
            });

        if (entity.getStatus() != AccountEntityStatus.OPEN) {
            log.error("Unable to operate on closed account={}", request.getId());
            throw new BadRequestAlertException(String.format("Invalid account status={%s}", entity.getStatus()));
        }

        if (!entity.getCurrency().equals(request.getCurrency())) {
            log.error("Account={} doesn't support currency={}", request.getId(), request.getCurrency());
            throw new UnsupportedCurrencyAlertException(request.getCurrency());
        }

        if (request.getOperationType() == BalanceOperationType.CREDIT) {
            entity.setBalance(
                entity.getBalance().add(request.getAmount())
            );
        } else { // handle debit operation
            if (entity.getBalance().compareTo(request.getAmount()) < 0) {
                log.error("Account={} has balance={} which is less than debit request amount: {}", entity.getId(), entity.getBalance(), request.getAmount());
                throw new InsufficientFundAlertException(request.getAmount());
            }

            entity.setBalance(
                entity.getBalance().subtract(request.getAmount())
            );
        }

        accountRepository.save(entity);
        accountTransactionLogRepository.save(
            accountTransactionLogMapper.toAccountTransactionLogEntity(request, entity)
        );

        return accountMapper.toAccountExchange(entity);
    }
}
