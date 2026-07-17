package ru.evgeny.asyncbookingsystem.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.evgeny.asyncbookingsystem.dto.CreateBookingRequest;
import ru.evgeny.asyncbookingsystem.exception.RetryableBookingProcessingException;
import ru.evgeny.asyncbookingsystem.exception.SlotAlreadyBookedException;
import ru.evgeny.asyncbookingsystem.rabbit.BookingEvent;

@Slf4j
@Service
@RequiredArgsConstructor
public class AsyncBookingProcessingService {

    public static final String CONSUMER_NAME = "booking-request-consumer";
    private static final String SLOT_ALREADY_BOOKED_REASON = "Slot already booked";
    private static final String UNEXPECTED_ERROR_REASON = "Unexpected booking processing error";

    private final BookingRequestLifecycleService bookingRequestLifecycleService;
    private final AsyncBookingFinalizationService asyncBookingFinalizationService;
    private final AsyncBookingAttemptService asyncBookingAttemptService;
    private final ProcessedMessageService processedMessageService;

    public void process(BookingEvent bookingEvent) {
        String requestId = bookingEvent.getRequestId();
        String messageId = bookingEvent.getMessageId();

        if (processedMessageService.isProcessed(messageId, CONSUMER_NAME)) {
            log.info("Skipping already processed booking event. requestId={}, messageId={}",
                    requestId, messageId);
            return;
        }

        if (!bookingRequestLifecycleService.exists(requestId)) {
            log.warn("Skipping booking event because request was not found. requestId={}, messageId={}",
                    requestId, messageId);
            return;
        }

        bookingRequestLifecycleService.markProcessing(requestId);

        try {
            failForRetryScenario(requestId);
            asyncBookingAttemptService.createBooking(buildCreateBookingRequest(bookingEvent));
            asyncBookingFinalizationService.markBooked(requestId, messageId, CONSUMER_NAME);
        } catch (SlotAlreadyBookedException exception) {
            asyncBookingFinalizationService.markRejected(
                    requestId,
                    SLOT_ALREADY_BOOKED_REASON,
                    messageId,
                    CONSUMER_NAME
            );
            log.info("Booking request rejected because slot is already booked. requestId={}, messageId={}",
                    requestId, messageId);
        } catch (RetryableBookingProcessingException exception) {
            throw exception;
        } catch (RuntimeException exception) {
            throw new RetryableBookingProcessingException(resolveFailureReason(exception), exception);
        }
    }

    public void markFailed(String requestId, String failureReason) {
        asyncBookingFinalizationService.markFailed(requestId, failureReason);
    }

    private CreateBookingRequest buildCreateBookingRequest(BookingEvent bookingEvent) {
        return new CreateBookingRequest(
                bookingEvent.getUserId(),
                bookingEvent.getResourceId(),
                bookingEvent.getStartTime(),
                bookingEvent.getEndTime()
        );
    }

    private String resolveFailureReason(RuntimeException exception) {
        String message = exception.getMessage();
        if (message == null || message.isBlank()) {
            return UNEXPECTED_ERROR_REASON;
        }

        return message;
    }

    private void failForRetryScenario(String requestId) {
        if (requestId.toLowerCase().contains("fail")) {
            throw new RetryableBookingProcessingException("Simulated technical failure");
        }
    }
}
