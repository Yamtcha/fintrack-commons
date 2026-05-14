package com.fintrack.common.domain;

public enum TransactionClass {
    PAYMENT,
    CHARGE,
    DEBT_PAYMENT,
    TRADE;

    @Override
    public String toString() {
        return name();
    }
}
