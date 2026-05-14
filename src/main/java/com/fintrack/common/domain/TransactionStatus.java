package com.fintrack.common.domain;

public enum TransactionStatus {
    PENDING,
    POSTED,
    CANCELLED;

    @Override
    public String toString() {
        return name();
    }
}
