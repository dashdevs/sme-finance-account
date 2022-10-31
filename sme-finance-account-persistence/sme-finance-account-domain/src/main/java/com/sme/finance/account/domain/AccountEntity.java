package com.sme.finance.account.domain;

import com.sme.finance.core.validation.CurrencyCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;

@Getter
@Setter
@Accessors(chain = true)
@Entity
@Table(name = "ACCOUNT")
public class AccountEntity {

    @Id
    @NotNull
    @Column(name = "ID", nullable = false)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ACCOUNT_SEQ")
    @SequenceGenerator(name = "ACCOUNT_SEQ", sequenceName = "ACCOUNT_SEQ", allocationSize = 1)
    private Long id;

    @NotNull
    @Length(min = 1, max = 255)
    @Column(name = "ACCOUNT_NUMBER", nullable = false)
    private String accountNumber;

    @NotNull
    @Pattern(regexp = "^\\d{3}$")
    @Column(name = "CURRENCY", nullable = false)
    private String currency;

    @NotNull
    @PositiveOrZero
    @Column(name = "BALANCE", nullable = false)
    private BigDecimal balance;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS", nullable = false)
    private AccountEntityStatus status;
}
