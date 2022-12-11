package com.ee.notificationservice.exception;

public class OrderProcessingException extends RuntimeException{
    private static final String MSG = "Error while processing Order";
    public OrderProcessingException() {
        super(MSG);
    }

}
