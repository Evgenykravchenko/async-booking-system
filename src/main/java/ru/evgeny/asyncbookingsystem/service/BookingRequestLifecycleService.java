package ru.evgeny.asyncbookingsystem.service;

import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.evgeny.asyncbookingsystem.entity.BookingRequestEntity;
import ru.evgeny.asyncbookingsystem.entity.BookingRequestStatus;
import ru.evgeny.asyncbookingsystem.exception.BookingRequestNotFoundException;
import ru.evgeny.asyncbookingsystem.repository.BookingRequestRepository;

@Service
@RequiredArgsConstructor
public class BookingRequestLifecycleService {

    private final BookingRequestRepository bookingRequestRepository;

    @Transactional
    public void markProcessing(String requestId) {
        updateStatus(requestId, BookingRequestStatus.PROCESSING, null);
    }

    @Transactional
    public void markBooked(String requestId) {
        updateStatus(requestId, BookingRequestStatus.BOOKED, null);
    }

    @Transactional
    public void markRejected(String requestId, String failureReason) {
        updateStatus(requestId, BookingRequestStatus.REJECTED, failureReason);
    }

    @Transactional
    public void markFailed(String requestId, String failureReason) {
        updateStatus(requestId, BookingRequestStatus.FAILED, failureReason);
    }

    @Transactional(readOnly = true)
    public boolean exists(String requestId) {
        return bookingRequestRepository.findByRequestId(requestId).isPresent();
    }

    private void updateStatus(String requestId, BookingRequestStatus status, String failureReason) {
        BookingRequestEntity bookingRequest = bookingRequestRepository.findByRequestId(requestId)
                .orElseThrow(() -> new BookingRequestNotFoundException(requestId));

        bookingRequest.setStatus(status);
        bookingRequest.setFailureReason(failureReason);
        bookingRequest.setUpdatedAt(LocalDateTime.now());
    }
}
