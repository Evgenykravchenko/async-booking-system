package ru.evgeny.asyncbookingsystem.exception;

import ru.evgeny.asyncbookingsystem.dto.CreateBookingRequest;

public class SlotAlreadyBookedException extends RuntimeException {

    private final CreateBookingRequest request;

    public SlotAlreadyBookedException(CreateBookingRequest request, Throwable cause) {
        super("Slot already booked", cause);
        this.request = request;
    }

    public CreateBookingRequest getRequest() {
        return request;
    }
}
