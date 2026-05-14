package com.fintrack.common.exception;

public class InvalidSourceTypeException extends RuntimeException {

    private final String sourceType;

    public InvalidSourceTypeException(String sourceType) {
        super("Invalid source type: " + sourceType);
        this.sourceType = sourceType;
    }

    public String getSourceType() {
        return sourceType;
    }
}