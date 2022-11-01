package com.sme.finance.account;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sme.finance.account.config.TestSecurityConfiguration;
import com.sme.finance.account.domain.AccountEntity;
import com.sme.finance.account.domain.AccountEntityStatus;
import com.sme.finance.account.repository.AccountRepository;
import com.sme.finance.account.rest.model.AccountExchange;
import com.sme.finance.account.rest.model.CheckAccountStatusResponse;
import com.sme.finance.core.it.annotation.IntegrationTest;
import com.sme.finance.core.security.AuthoritiesConstants;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@IntegrationTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@WithMockUser(authorities = AuthoritiesConstants.ADMIN)
@SpringBootTest(classes = {FinanceAccountApp.class, TestSecurityConfiguration.class}, webEnvironment = RANDOM_PORT)
class AccountsIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AccountRepository accountRepository;

    @BeforeEach
    void setUp(TestInfo testInfo) {
        System.out.println(this.getClass().getSimpleName() + "::" + testInfo.getTestMethod().map(Method::getName) + " has started");
    }

    @AfterEach
    void tearDown(TestInfo testInfo) {
        System.out.println(this.getClass().getSimpleName() + "::" + testInfo.getTestMethod().map(Method::getName) + " has finished");
    }

    @Test
    void shouldCheckStatus() throws Exception {
        // Given
        AccountEntity entity = accountRepository.save(getEntity());

        // When
        String contentAsString = mockMvc.perform(get("/api/accounts/" + entity.getId() + "/check-status"))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andReturn()
            .getResponse()
            .getContentAsString();

        CheckAccountStatusResponse response = objectMapper.readValue(contentAsString, CheckAccountStatusResponse.class);

        // Then
        assertEquals(entity.getId(), response.getId());
        assertEquals(entity.getStatus().name(), response.getStatus().name());
    }

    @Test
    void shouldReturn404WhileCheckStatus() throws Exception {
        // Then
        Exception resolvedException = mockMvc.perform(get("/api/accounts/0/check-status"))
            .andDo(print())
            .andExpect(status().isNotFound())
            .andExpect(content().contentType("application/problem+json"))
            .andReturn()
            .getResolvedException();

        assertEquals("Not found: Account=0 doesn't exist", resolvedException.getMessage());
    }

    @Test
    void shouldCheckBalance() throws Exception {
        // Given
        AccountEntity entity = accountRepository.save(getEntity());

        // When
        String contentAsString = mockMvc.perform(get("/api/accounts/" + entity.getId() + "/check-balance"))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andReturn()
            .getResponse()
            .getContentAsString();

        AccountExchange response = objectMapper.readValue(contentAsString, AccountExchange.class);

        // Then
        assertEquals(entity.getId(), response.getId());
        assertEquals(entity.getBalance(), response.getBalance());
        assertEquals(entity.getStatus().name(), response.getStatus().name());
    }

    @Test
    void shouldReturn404WhileCheckBalance() throws Exception {
        // Then
        Exception resolvedException = mockMvc.perform(get("/api/accounts/0/check-balance"))
            .andDo(print())
            .andExpect(status().isNotFound())
            .andExpect(content().contentType("application/problem+json"))
            .andReturn()
            .getResolvedException();

        assertEquals("Not found: Account=0 doesn't exist", resolvedException.getMessage());
    }


    private AccountEntity getEntity() {
        return new AccountEntity()
            .setAccountNumber(UUID.randomUUID().toString())
            .setBalance(BigDecimal.TEN.setScale(2, RoundingMode.UNNECESSARY))
            .setCurrency("978")
            .setStatus(AccountEntityStatus.OPEN);
    }

}
