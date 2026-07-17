package ru.evgeny.asyncbookingsystem.rabbit;

import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import ru.evgeny.asyncbookingsystem.config.RabbitConfig;

@Component
@RequiredArgsConstructor
public class BookingEventProducer {

    private final RabbitTemplate rabbitTemplate;

    public void publishBookingRequested(BookingEvent bookingEvent) {
        publish(bookingEvent, RabbitConfig.BOOKING_REQUESTED_ROUTING_KEY, Map.of());
    }

    public void publishBookingRetry(BookingEvent bookingEvent, int attempt) {
        publish(
                bookingEvent,
                RabbitConfig.BOOKING_RETRY_ROUTING_KEY,
                Map.of(RabbitConfig.RETRY_ATTEMPT_HEADER, attempt)
        );
    }

    public void publishBookingFailed(BookingEvent bookingEvent) {
        publish(bookingEvent, RabbitConfig.BOOKING_FAILED_ROUTING_KEY, Map.of());
    }

    private void publish(BookingEvent bookingEvent, String routingKey, Map<String, Object> headers) {
        rabbitTemplate.convertAndSend(
                RabbitConfig.BOOKING_EXCHANGE,
                routingKey,
                bookingEvent,
                message -> {
                    headers.forEach((key, value) -> message.getMessageProperties().setHeader(key, value));
                    return message;
                }
        );
    }
}
