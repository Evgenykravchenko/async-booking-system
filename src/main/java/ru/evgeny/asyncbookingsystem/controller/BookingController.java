package ru.evgeny.asyncbookingsystem.controller;

import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.evgeny.asyncbookingsystem.dto.BookingResponse;
import ru.evgeny.asyncbookingsystem.dto.CreateBookingRequest;
import ru.evgeny.asyncbookingsystem.service.BookingService;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping("/sync")
    public BookingResponse createBooking(@Valid @RequestBody CreateBookingRequest request) {
        return bookingService.createBooking(request);
    }

    @GetMapping
    public List<BookingResponse> getBookings(@RequestParam(required = false) Long resourceId) {
        return bookingService.getBookings(resourceId);
    }
}
