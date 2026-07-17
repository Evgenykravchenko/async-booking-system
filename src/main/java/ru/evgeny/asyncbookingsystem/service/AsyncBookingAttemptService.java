package ru.evgeny.asyncbookingsystem.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.evgeny.asyncbookingsystem.dto.CreateBookingRequest;

@Service
@RequiredArgsConstructor
public class AsyncBookingAttemptService {

    private final BookingService bookingService;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void createBooking(CreateBookingRequest request) {
        bookingService.createBooking(request);
    }
}
