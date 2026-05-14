package com.fintrack.common.domain;

public enum TransactionType {
    DEBIT,
    CREDIT,
    TRANSFER,
    REFUND;

    @Override
    public String toString() {
        return name();
    }
}
