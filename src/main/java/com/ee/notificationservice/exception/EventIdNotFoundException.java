package com.ee.notificationservice.exception;

public class EventIdNotFoundException extends RuntimeException {

    private static final String MSG = "Order Id/Event Id not present";
    public EventIdNotFoundException() {
        super(MSG);
    }

    public EventIdNotFoundException(String message) {
        super(message);
    }

    public EventIdNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public EventIdNotFoundException(Throwable cause) {
        super(cause);
    }

    protected EventIdNotFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
