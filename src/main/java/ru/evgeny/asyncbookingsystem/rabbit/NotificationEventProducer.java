package ru.evgeny.asyncbookingsystem.rabbit;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import ru.evgeny.asyncbookingsystem.config.RabbitConfig;

@Component
@RequiredArgsConstructor
public class NotificationEventProducer {

    private final RabbitTemplate rabbitTemplate;

    public void publishBookingConfirmed(BookingNotificationEvent notificationEvent) {
        rabbitTemplate.convertAndSend(
                RabbitConfig.BOOKING_EXCHANGE,
                RabbitConfig.BOOKING_CONFIRMED_ROUTING_KEY,
                notificationEvent
        );
    }
}
