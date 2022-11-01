package com.sme.finance.account.domain;

import java.util.Arrays;

public enum AccountTransactionLogEntityType {
    DEBIT,
    CREDIT;

    public static AccountTransactionLogEntityType from(final String typeValue) {
        return Arrays.stream(values())
            .filter(type -> type.name().equals(typeValue))
            .findAny()
            .orElseThrow(() -> new IllegalArgumentException("Unexpected value '" + typeValue + "'"));
    }
}
