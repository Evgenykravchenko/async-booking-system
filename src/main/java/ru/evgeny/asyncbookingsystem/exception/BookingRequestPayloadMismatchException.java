package ru.evgeny.asyncbookingsystem.exception;

public class BookingRequestPayloadMismatchException extends RuntimeException {

    public BookingRequestPayloadMismatchException(String requestId) {
        super("Request with id '" + requestId + "' already exists with a different payload");
    }
}
