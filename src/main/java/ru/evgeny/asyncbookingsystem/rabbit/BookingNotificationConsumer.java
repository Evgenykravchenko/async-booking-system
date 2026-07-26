package ru.evgeny.asyncbookingsystem.rabbit;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import ru.evgeny.asyncbookingsystem.config.RabbitConfig;

@Slf4j
@Component
public class BookingNotificationConsumer {

    @RabbitListener(queues = RabbitConfig.BOOKING_NOTIFICATION_QUEUE)
    public void consume(BookingNotificationEvent notificationEvent) {
        log.info(
                "Booking confirmation notification processed. requestId={}, userId={}, resourceId={}, startTime={}, endTime={}",
                notificationEvent.getRequestId(),
                notificationEvent.getUserId(),
                notificationEvent.getResourceId(),
                notificationEvent.getStartTime(),
                notificationEvent.getEndTime()
        );
    }
}
