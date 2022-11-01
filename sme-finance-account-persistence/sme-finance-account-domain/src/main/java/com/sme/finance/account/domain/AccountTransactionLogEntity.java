package com.sme.finance.account.domain;

import com.sme.finance.core.validation.CurrencyCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Setter
@Accessors(chain = true)
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "ACCOUNT_TRANSACTION_LOG")
public class AccountTransactionLogEntity {

    @Id
    @NotNull
    @Column(name = "ID", nullable = false)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ACCOUNT_TRANSACTION_LOG_SEQ")
    @SequenceGenerator(name = "ACCOUNT_TRANSACTION_LOG_SEQ", sequenceName = "ACCOUNT_TRANSACTION_LOG_SEQ", allocationSize = 1)
    private Long id;

    @NotNull
    @CurrencyCode
    @Column(name = "CURRENCY", nullable = false)
    private String currency;

    @NotNull
    @PositiveOrZero
    @Column(name = "AMOUNT", nullable = false)
    private BigDecimal amount;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "TYPE", nullable = false)
    private AccountTransactionLogEntityType type;

    @CreatedDate
    @Column(name = "CREATED_DATE", nullable = false, updatable = false)
    private Instant createdDate;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "ACCOUNT_ID", nullable = false, foreignKey = @ForeignKey(name = "FK_ACCOUNT_ID_ON_ACCOUNT_TRANSACTION_LOG"))
    private AccountEntity account;

}
