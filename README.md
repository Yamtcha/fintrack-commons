# fintrack-common

Shared library for the FinTrack financial transaction aggregation platform. Provides domain enums, transaction models, events, DTOs, exceptions, and utilities used across all services.

Produces a plain `.jar` — no Spring Boot, no main class.

---

## Coordinates

```xml
<dependency>
    <groupId>com.fintrack</groupId>
    <artifactId>fintrack-common</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>
```

---

## Build

```bash
mvn clean install
```

Requires Java 21 and Maven 3.9+.

---

## Module structure

```
fintrack-commons/
├── pom.xml
└── src/main/java/com/fintrack/common/
    ├── domain/
    │   ├── SourceType.java
    │   ├── TransactionClass.java
    │   ├── TransactionType.java
    │   └── TransactionStatus.java
    ├── model/
    │   └── Transaction.java
    ├── events/
    │   └── TransactionIngestedEvent.java
    ├── dto/
    │   ├── ApiResponse.java
    │   └── ErrorResponse.java
    ├── exception/
    │   ├── SourceNotFoundException.java
    │   ├── DuplicateTransactionException.java
    │   └── InvalidSourceTypeException.java
    └── util/
        └── TransactionClassResolver.java
```

---

## Dependencies

| Dependency | Version | Scope | Purpose |
|---|---|---|---|
| `jackson-databind` | 2.17.1 | compile | JSON serialization / deserialization |
| `jackson-datatype-jsr310` | 2.17.1 | compile | `java.time` support for Jackson |
| `lombok` | 1.18.32 | provided | Boilerplate reduction (`@Builder`, `@Data`, etc.) |
| `jakarta.validation-api` | 3.1.0 | compile | Bean Validation annotations |

---

## Packages

### `com.fintrack.common.domain` — enums

All enums override `toString()` to return `name()`.

#### `SourceType`

Represents the financial source system a transaction originated from.

| Value |
|---|
| `CREDIT` |
| `DEBIT` |
| `LOANS` |
| `INVESTMENTS` |

#### `TransactionClass`

High-level classification describing the economic role of a transaction.

| Value | Source | Meaning |
|---|---|---|
| `PAYMENT` | `DEBIT` | Everyday bank spend or income |
| `CHARGE` | `CREDIT` | Charge against a credit line (spend side) |
| `DEBT_PAYMENT` | `CREDIT` / `LOANS` | Reduces a liability |
| `TRADE` | `INVESTMENTS` | Asset acquisition or disposal |

#### `TransactionType`

Directional flow or nature of a transaction.

| Value |
|---|
| `DEBIT` |
| `CREDIT` |
| `TRANSFER` |
| `REFUND` |

#### `TransactionStatus`

Lifecycle status as reported by the source system.

| Value |
|---|
| `PENDING` |
| `POSTED` |
| `CANCELLED` |

---

### `com.fintrack.common.model` — transaction model

#### `Transaction`

Normalized representation of a financial transaction. Annotated with `@Data @Builder @NoArgsConstructor @AllArgsConstructor`. No JPA annotations.

| Field | Type | Notes |
|---|---|---|
| `id` | `UUID` | Internal identifier |
| `externalId` | `String` | ID assigned by the source system |
| `sourceId` | `String` | Identifies the connected account / source |
| `sourceType` | `SourceType` | Origin system category |
| `transactionClass` | `TransactionClass` | High-level economic classification |
| `currency` | `String` | ISO 4217 code |
| `amount` | `BigDecimal` | Transaction amount |
| `description` | `String` | Raw description from source |
| `merchantName` | `String` | Merchant name as reported |
| `type` | `TransactionType` | Directional flow |
| `status` | `TransactionStatus` | Lifecycle status |
| `transactedAt` | `Instant` | When the transaction occurred (ISO 8601) |

`transactedAt` is annotated with `@JsonSerialize(using = InstantSerializer.class)` / `@JsonDeserialize(using = InstantDeserializer.class)`.

---

### `com.fintrack.common.events` — domain events

#### `TransactionIngestedEvent`

Published when a transaction has been ingested and normalized. Annotated with `@Data @Builder @NoArgsConstructor @AllArgsConstructor`.

| Field | Type | Notes |
|---|---|---|
| `eventId` | `UUID` | Unique event identifier |
| `occurredAt` | `Instant` | Event creation timestamp (ISO 8601) |
| `transaction` | `Transaction` | The normalized transaction payload |
| `targetAggregators` | `List<String>` | Aggregator services that should process this event |
| `batchId` | `String` | Originating batch idempotency key |

`occurredAt` is Jackson-annotated for ISO 8601 serialization.

---

### `com.fintrack.common.dto` — API DTOs (Java records)

#### `ApiResponse<T>`

Generic wrapper for all API responses.

| Field | Type | Notes |
|---|---|---|
| `success` | `boolean` | `true` on success, `false` on error |
| `data` | `T` | Payload; `null` on error |
| `requestId` | `String` | Correlation ID |
| `timestamp` | `Instant` | Response creation time (ISO 8601) |

Static factories:

```java
// Wraps data, generates a random requestId, sets success=true
ApiResponse<MyType> ok = ApiResponse.of(data);

// Sets success=false, data=null, uses provided requestId
ApiResponse<Void> err = ApiResponse.error(requestId);
```

#### `ErrorResponse`

Structured error payload for API error responses.

| Field | Type |
|---|---|
| `code` | `String` |
| `message` | `String` |
| `requestId` | `String` |
| `timestamp` | `Instant` (ISO 8601) |

---

### `com.fintrack.common.exception` — runtime exceptions

All extend `RuntimeException`. Each stores the offending value as a field and includes it in `getMessage()`.

| Exception | Field | Getter | Message |
|---|---|---|---|
| `SourceNotFoundException` | `String sourceId` | `getSourceId()` | `"Source not found: <sourceId>"` |
| `DuplicateTransactionException` | `String fingerprint` | `getFingerprint()` | `"Duplicate transaction detected: <fingerprint>"` |
| `InvalidSourceTypeException` | `String sourceType` | `getSourceType()` | `"Invalid source type: <sourceType>"` |

---

### `com.fintrack.common.util` — utilities

#### `TransactionClassResolver`

Resolves `TransactionClass` from a `SourceType` and an optional kind hint. All methods are static; the class is not instantiable.

```java
TransactionClassResolver.resolve(SourceType.DEBIT, null);           // PAYMENT
TransactionClassResolver.resolve(SourceType.LOANS, null);           // DEBT_PAYMENT
TransactionClassResolver.resolve(SourceType.INVESTMENTS, null);     // TRADE
TransactionClassResolver.resolve(SourceType.CREDIT, "payment");     // DEBT_PAYMENT
TransactionClassResolver.resolve(SourceType.CREDIT, "purchase");    // CHARGE
```

Resolution rules:

| `SourceType` | `kind` | Result |
|---|---|---|
| `DEBIT` | any | `PAYMENT` |
| `LOANS` | any | `DEBT_PAYMENT` |
| `INVESTMENTS` | any | `TRADE` |
| `CREDIT` | `"payment"` (case-insensitive) | `DEBT_PAYMENT` |
| `CREDIT` | anything else | `CHARGE` |

---

## Design notes

- No Spring annotations (`@Component`, `@Service`, `@Bean`, etc.) — pure Java library.
- No JPA annotations — `Transaction` is a shared model, not a database entity.
- `Instant` fields serialize as ISO 8601 strings via `jackson-datatype-jsr310`.
