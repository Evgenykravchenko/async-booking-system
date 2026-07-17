package ru.evgeny.asyncbookingsystem.config;

import java.util.Map;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    public static final String BOOKING_EXCHANGE = "booking.exchange";
    public static final String BOOKING_REQUEST_QUEUE = "booking.request.queue";
    public static final String BOOKING_RETRY_QUEUE = "booking.retry.queue";
    public static final String BOOKING_DLQ = "booking.dlq";
    public static final String BOOKING_REQUESTED_ROUTING_KEY = "booking.requested";
    public static final String BOOKING_RETRY_ROUTING_KEY = "booking.retry";
    public static final String BOOKING_FAILED_ROUTING_KEY = "booking.failed";
    public static final String RETRY_ATTEMPT_HEADER = "x-retry-attempt";
    public static final long BOOKING_RETRY_TTL_MILLIS = 2_000L;
    public static final int BOOKING_MAX_RETRY_ATTEMPTS = 3;

    @Bean
    public DirectExchange bookingExchange() {
        return new DirectExchange(BOOKING_EXCHANGE);
    }

    @Bean
    public Queue bookingRequestQueue() {
        return new Queue(
                BOOKING_REQUEST_QUEUE,
                true,
                false,
                false,
                Map.of(
                        "x-dead-letter-exchange", BOOKING_EXCHANGE,
                        "x-dead-letter-routing-key", BOOKING_FAILED_ROUTING_KEY
                )
        );
    }

    @Bean
    public Queue bookingRetryQueue() {
        return new Queue(
                BOOKING_RETRY_QUEUE,
                true,
                false,
                false,
                Map.of(
                        "x-message-ttl", BOOKING_RETRY_TTL_MILLIS,
                        "x-dead-letter-exchange", BOOKING_EXCHANGE,
                        "x-dead-letter-routing-key", BOOKING_REQUESTED_ROUTING_KEY
                )
        );
    }

    @Bean
    public Queue bookingDlq() {
        return new Queue(BOOKING_DLQ, true);
    }

    @Bean
    public Binding bookingRequestBinding(Queue bookingRequestQueue, DirectExchange bookingExchange) {
        return BindingBuilder.bind(bookingRequestQueue)
                .to(bookingExchange)
                .with(BOOKING_REQUESTED_ROUTING_KEY);
    }

    @Bean
    public Binding bookingRetryBinding(Queue bookingRetryQueue, DirectExchange bookingExchange) {
        return BindingBuilder.bind(bookingRetryQueue)
                .to(bookingExchange)
                .with(BOOKING_RETRY_ROUTING_KEY);
    }

    @Bean
    public Binding bookingDlqBinding(Queue bookingDlq, DirectExchange bookingExchange) {
        return BindingBuilder.bind(bookingDlq)
                .to(bookingExchange)
                .with(BOOKING_FAILED_ROUTING_KEY);
    }

    @Bean
    public MessageConverter rabbitMessageConverter() {
        return new JacksonJsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(
            ConnectionFactory connectionFactory,
            MessageConverter rabbitMessageConverter
    ) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(rabbitMessageConverter);
        return rabbitTemplate;
    }
}
