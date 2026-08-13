package com.iftrue.delivery.domain.common;

public final class DomainValidator {

    private DomainValidator() {
    }

    public static <T> T requireNonNull(
            T value,
            String message
    ) {
        if (value == null) {
            throw new IllegalArgumentException(message);
        }

        return value;
    }

    public static String requireText(
            String value,
            String message
    ) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(message);
        }

        return value;
    }

    public static int requirePositive(
            int value,
            String message
    ) {
        if (value <= 0) {
            throw new IllegalArgumentException(message);
        }

        return value;
    }
}
