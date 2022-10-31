package com.sme.finance.account.error;

import com.sme.finance.core.error.ErrorConstants;
import lombok.Getter;
import org.zalando.problem.AbstractThrowableProblem;
import org.zalando.problem.Status;

import java.math.BigDecimal;

@Getter
@SuppressWarnings("java:S110") // Inheritance tree of classes should not be too deep
public class InsufficientFundAlertException extends AbstractThrowableProblem {

    public InsufficientFundAlertException(BigDecimal debitAmount) {
        super(ErrorConstants.DEFAULT_TYPE, "Insufficient funds", Status.BAD_REQUEST, String.format("Account balance is less than %s", debitAmount));
    }
}
