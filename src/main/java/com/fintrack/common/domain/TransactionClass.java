package com.fintrack.common.domain;

public enum TransactionClass {
    SPENDING,
    DEBT_PAYMENT,
    TRADE;

    @Override
    public String toString() {
        return name();
    }
}
