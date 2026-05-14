package com.fintrack.common.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.InstantDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.InstantSerializer;

import java.time.Instant;
import java.util.UUID;

public record ApiResponse<T>(
        boolean success,
        T data,
        String requestId,
        @JsonSerialize(using = InstantSerializer.class)
        @JsonDeserialize(using = InstantDeserializer.class)
        Instant timestamp
) {

    public static <T> ApiResponse<T> of(T data) {
        return new ApiResponse<>(true, data, UUID.randomUUID().toString(), Instant.now());
    }


    public static <T> ApiResponse<T> error(String requestId) {
        return new ApiResponse<>(false, null, requestId, Instant.now());
    }
}
