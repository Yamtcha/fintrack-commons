package com.fintrack.common.util;

import com.fintrack.common.domain.SourceType;
import com.fintrack.common.domain.TransactionClass;

public final class TransactionClassResolver {

    private TransactionClassResolver() {}

    public static TransactionClass resolve(SourceType sourceType, String kind) {
        return switch (sourceType) {
            case DEBIT -> TransactionClass.PAYMENT;
            case LOANS -> TransactionClass.DEBT_PAYMENT;
            case INVESTMENTS -> TransactionClass.TRADE;
            case CREDIT -> "payment".equalsIgnoreCase(kind)
                    ? TransactionClass.DEBT_PAYMENT
                    : TransactionClass.CHARGE;
        };
    }
}
