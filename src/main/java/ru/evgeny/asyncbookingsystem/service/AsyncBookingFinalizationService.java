package ru.evgeny.asyncbookingsystem.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AsyncBookingFinalizationService {

    private final BookingRequestLifecycleService bookingRequestLifecycleService;
    private final ProcessedMessageService processedMessageService;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void markBooked(String requestId, String messageId, String consumerName) {
        bookingRequestLifecycleService.markBooked(requestId);
        processedMessageService.markProcessed(messageId, consumerName);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void markRejected(String requestId, String failureReason, String messageId, String consumerName) {
        bookingRequestLifecycleService.markRejected(requestId, failureReason);
        processedMessageService.markProcessed(messageId, consumerName);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void markFailed(String requestId, String failureReason) {
        bookingRequestLifecycleService.markFailed(requestId, failureReason);
    }
}
