package ru.evgeny.asyncbookingsystem.exception;

public class BookingRequestNotFoundException extends RuntimeException {

    public BookingRequestNotFoundException(String requestId) {
        super("Booking request with requestId " + requestId + " was not found");
    }
}
