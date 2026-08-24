package com.iftrue.notification.application.exception;

public class AiDeadlineResponseParseException extends RuntimeException {

    public AiDeadlineResponseParseException(String message) {
        super(message);
    }

    public AiDeadlineResponseParseException(
            String message,
            Throwable cause
    ) {
        super(message, cause);
    }
}
