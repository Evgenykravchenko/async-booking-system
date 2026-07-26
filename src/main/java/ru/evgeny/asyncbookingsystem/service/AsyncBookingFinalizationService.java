package ru.evgeny.asyncbookingsystem.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import ru.evgeny.asyncbookingsystem.entity.BookingRequestEntity;
import ru.evgeny.asyncbookingsystem.rabbit.BookingNotificationEvent;
import ru.evgeny.asyncbookingsystem.rabbit.NotificationEventProducer;

@Service
@RequiredArgsConstructor
public class AsyncBookingFinalizationService {

    private final BookingRequestLifecycleService bookingRequestLifecycleService;
    private final ProcessedMessageService processedMessageService;
    private final NotificationEventProducer notificationEventProducer;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void markBooked(String requestId, String messageId, String consumerName) {
        BookingRequestEntity bookingRequest = bookingRequestLifecycleService.markBooked(requestId);
        processedMessageService.markProcessed(messageId, consumerName);
        publishBookingConfirmedEvent(bookingRequest);
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

    private void publishBookingConfirmedEvent(BookingRequestEntity bookingRequest) {
        BookingNotificationEvent notificationEvent = BookingNotificationEvent.builder()
                .requestId(bookingRequest.getRequestId())
                .resourceId(bookingRequest.getResource().getId())
                .userId(bookingRequest.getUserId())
                .startTime(bookingRequest.getStartTime())
                .endTime(bookingRequest.getEndTime())
                .build();

        if (!TransactionSynchronizationManager.isActualTransactionActive()) {
            notificationEventProducer.publishBookingConfirmed(notificationEvent);
            return;
        }

        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                notificationEventProducer.publishBookingConfirmed(notificationEvent);
            }
        });
    }
}
