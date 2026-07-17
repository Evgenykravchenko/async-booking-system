package ru.evgeny.asyncbookingsystem.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.evgeny.asyncbookingsystem.dto.AsyncBookingRequest;
import ru.evgeny.asyncbookingsystem.dto.BookingRequestResponse;
import ru.evgeny.asyncbookingsystem.service.BookingRequestService;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class BookingRequestController {

    private final BookingRequestService bookingRequestService;

    @PostMapping("/api/bookings/async")
    public BookingRequestResponse createAsyncBookingRequest(@Valid @RequestBody AsyncBookingRequest request) {
        return bookingRequestService.createBookingRequest(request);
    }

    @GetMapping("/api/booking-requests/{requestId}")
    public BookingRequestResponse getBookingRequest(@PathVariable String requestId) {
        return bookingRequestService.getByRequestId(requestId);
    }
}
