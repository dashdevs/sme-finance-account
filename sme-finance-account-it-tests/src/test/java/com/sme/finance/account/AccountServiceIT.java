package com.sme.finance.account;

import com.sme.finance.account.config.TestSecurityConfiguration;
import com.sme.finance.account.domain.AccountEntity;
import com.sme.finance.account.domain.AccountEntityStatus;
import com.sme.finance.account.repository.AccountRepository;
import com.sme.finance.core.it.annotation.IntegrationTest;
import org.hibernate.StaleObjectStateException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ActiveProfiles("test")
@IntegrationTest
@SpringBootTest(classes = {FinanceAccountApp.class, TestSecurityConfiguration.class})
class AccountServiceIT {

    @Autowired
    private AccountRepository accountRepository;

    @Test
    void shouldHandleOptimisticLocking() throws InterruptedException {
        // Given
        AccountEntity entity = accountRepository.save(
            new AccountEntity()
                .setAccountNumber(UUID.randomUUID().toString())
                .setBalance(BigDecimal.TEN.setScale(2, RoundingMode.UNNECESSARY))
                .setCurrency("978")
                .setVersion(1L)
                .setStatus(AccountEntityStatus.OPEN)
        );

        AtomicReference<Throwable> throwableAtomicReference = new AtomicReference<>();
        Thread thread = new Thread(() -> {
            entity.setVersion(0L);
            entity.setBalance(BigDecimal.ZERO);

            accountRepository.save(entity);
        });
        thread.setUncaughtExceptionHandler((t, throwable) -> throwableAtomicReference.set(throwable));

        // When
        thread.start();
        thread.join();

        // Then
        assertNotNull(throwableAtomicReference.get());
        assertEquals(ObjectOptimisticLockingFailureException.class, throwableAtomicReference.get().getClass());
        assertEquals(StaleObjectStateException.class, throwableAtomicReference.get().getCause().getClass());
    }
}
