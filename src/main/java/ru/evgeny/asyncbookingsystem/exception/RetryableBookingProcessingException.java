package ru.evgeny.asyncbookingsystem.exception;

public class RetryableBookingProcessingException extends RuntimeException {

    public RetryableBookingProcessingException(String message) {
        super(message);
    }

    public RetryableBookingProcessingException(String message, Throwable cause) {
        super(message, cause);
    }
}
