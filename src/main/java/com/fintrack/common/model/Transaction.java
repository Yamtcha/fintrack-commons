package com.fintrack.common.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.InstantSerializer;
import com.fasterxml.jackson.datatype.jsr310.deser.InstantDeserializer;
import com.fintrack.common.domain.SourceType;
import com.fintrack.common.domain.TransactionStatus;
import com.fintrack.common.domain.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {

    private UUID id;

    private String externalId;

    private String sourceId;

    private SourceType sourceType;

    private String currency;

    private BigDecimal amount;

    private String description;

    private String merchantName;

    private TransactionType type;

    private TransactionStatus status;

    @JsonSerialize(using = InstantSerializer.class)
    @JsonDeserialize(using = InstantDeserializer.class)
    private Instant transactedAt;
}