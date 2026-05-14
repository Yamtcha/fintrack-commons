package com.fintrack.common.domain;

public enum SourceType {
    CREDIT,
    DEBIT,
    LOANS,
    INVESTMENTS;

    @Override
    public String toString() {
        return name();
    }
}
