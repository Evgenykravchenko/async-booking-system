package ru.evgeny.asyncbookingsystem.rabbit;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import ru.evgeny.asyncbookingsystem.config.RabbitConfig;
import ru.evgeny.asyncbookingsystem.service.AsyncBookingProcessingService;

@Slf4j
@Component
@RequiredArgsConstructor
public class BookingRequestConsumer {

    private final AsyncBookingProcessingService asyncBookingProcessingService;

    @RabbitListener(queues = RabbitConfig.BOOKING_REQUEST_QUEUE)
    public void consume(BookingEvent bookingEvent) {
        log.info("Received booking event. requestId={}, messageId={}",
                bookingEvent.getRequestId(), bookingEvent.getMessageId());
        asyncBookingProcessingService.process(bookingEvent);
    }
}
