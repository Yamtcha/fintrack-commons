package com.fintrack.common.exception;

public class DuplicateTransactionException extends RuntimeException {

    private final String fingerprint;

    public DuplicateTransactionException(String fingerprint) {
        super("Duplicate transaction detected: " + fingerprint);
        this.fingerprint = fingerprint;
    }

    public String getFingerprint() {
        return fingerprint;
    }
}