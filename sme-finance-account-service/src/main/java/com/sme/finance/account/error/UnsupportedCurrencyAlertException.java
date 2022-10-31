package com.sme.finance.account.error;

import com.sme.finance.core.error.ErrorConstants;
import lombok.Getter;
import org.zalando.problem.AbstractThrowableProblem;
import org.zalando.problem.Status;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

@Getter
@SuppressWarnings("java:S110") // Inheritance tree of classes should not be too deep
public class UnsupportedCurrencyAlertException extends AbstractThrowableProblem {

    private final String entityName;
    private final String errorKey;

    public UnsupportedCurrencyAlertException(String entityName, String errorKey) {
        this(ErrorConstants.DEFAULT_TYPE, "UNSUPPORTED CURRENCY", entityName, errorKey);
    }

    public UnsupportedCurrencyAlertException(URI type, String defaultMessage, String entityName, String errorKey) {
        super(type, defaultMessage, Status.BAD_REQUEST, String.format("%s '%s' unsupported currency ", entityName, errorKey), null, null, getAlertParameters(entityName, errorKey));
        this.entityName = entityName;
        this.errorKey = errorKey;
    }

    private static Map<String, Object> getAlertParameters(String entityName, String errorKey) {
        final Map<String, Object> parameters = new HashMap<>();

        parameters.put("message", "error." + errorKey);
        parameters.put("params", entityName);

        return parameters;
    }
}
