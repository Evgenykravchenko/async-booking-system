package ru.evgeny.asyncbookingsystem.rabbit;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import ru.evgeny.asyncbookingsystem.config.RabbitConfig;

@Component
@RequiredArgsConstructor
public class BookingEventProducer {

    private final RabbitTemplate rabbitTemplate;

    public void publishBookingRequested(BookingEvent bookingEvent) {
        rabbitTemplate.convertAndSend(
                RabbitConfig.BOOKING_EXCHANGE,
                RabbitConfig.BOOKING_REQUESTED_ROUTING_KEY,
                bookingEvent
        );
    }
}
