package com.sme.finance.account.error;

import com.sme.finance.core.error.ErrorConstants;
import lombok.Getter;
import org.zalando.problem.AbstractThrowableProblem;
import org.zalando.problem.Status;

@Getter
@SuppressWarnings("java:S110") // Inheritance tree of classes should not be too deep
public class UnsupportedCurrencyAlertException extends AbstractThrowableProblem {

    public UnsupportedCurrencyAlertException(String currency) {
        super(ErrorConstants.DEFAULT_TYPE, "Unsupported currency", Status.BAD_REQUEST, String.format("Account doesn't support currency={%s}", currency));
    }
}
