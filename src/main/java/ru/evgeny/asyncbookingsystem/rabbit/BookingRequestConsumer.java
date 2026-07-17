package ru.evgeny.asyncbookingsystem.rabbit;

import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import ru.evgeny.asyncbookingsystem.config.RabbitConfig;
import ru.evgeny.asyncbookingsystem.exception.RetryableBookingProcessingException;
import ru.evgeny.asyncbookingsystem.service.AsyncBookingProcessingService;

@Slf4j
@Component
@RequiredArgsConstructor
public class BookingRequestConsumer {

    private final AsyncBookingProcessingService asyncBookingProcessingService;
    private final BookingEventProducer bookingEventProducer;

    @RabbitListener(queues = RabbitConfig.BOOKING_REQUEST_QUEUE)
    public void consume(BookingEvent bookingEvent, Message message) {
        log.info("Received booking event. requestId={}, messageId={}",
                bookingEvent.getRequestId(), bookingEvent.getMessageId());

        try {
            asyncBookingProcessingService.process(bookingEvent);
        } catch (RetryableBookingProcessingException exception) {
            handleRetryableFailure(bookingEvent, message, exception);
        }
    }

    private void handleRetryableFailure(
            BookingEvent bookingEvent,
            Message message,
            RetryableBookingProcessingException exception
    ) {
        int currentAttempt = extractRetryAttempt(message);

        if (currentAttempt >= RabbitConfig.BOOKING_MAX_RETRY_ATTEMPTS) {
            asyncBookingProcessingService.markFailed(bookingEvent.getRequestId(), exception.getMessage());
            bookingEventProducer.publishBookingFailed(bookingEvent);
            log.error("Booking request moved to DLQ after retry limit. requestId={}, messageId={}, attempts={}",
                    bookingEvent.getRequestId(), bookingEvent.getMessageId(), currentAttempt, exception);
            return;
        }

        bookingEventProducer.publishBookingRetry(bookingEvent, currentAttempt + 1);
        log.warn("Booking request moved to retry queue. requestId={}, messageId={}, attempts={}",
                bookingEvent.getRequestId(), bookingEvent.getMessageId(), currentAttempt, exception);
    }

    private int extractRetryAttempt(Message message) {
        Object retryAttemptHeader = message.getMessageProperties().getHeaders().get(RabbitConfig.RETRY_ATTEMPT_HEADER);
        if (retryAttemptHeader instanceof Number retryAttempt) {
            return retryAttempt.intValue();
        }

        List<Map<String, ?>> xDeathHeader = message.getMessageProperties().getXDeathHeader();
        if (xDeathHeader == null) {
            return 1;
        }

        return xDeathHeader.stream()
                .filter(entry -> RabbitConfig.BOOKING_RETRY_QUEUE.equals(entry.get("queue")))
                .findFirst()
                .map(entry -> entry.get("count"))
                .filter(Number.class::isInstance)
                .map(Number.class::cast)
                .map(Number::intValue)
                .map(count -> count + 1)
                .orElse(1);
    }
}
