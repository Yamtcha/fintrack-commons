package com.fintrack.common.exception;

public class SourceNotFoundException extends RuntimeException {

    private final String sourceId;

    public SourceNotFoundException(String sourceId) {
        super("Source not found: " + sourceId);
        this.sourceId = sourceId;
    }

    public String getSourceId() {
        return sourceId;
    }
}