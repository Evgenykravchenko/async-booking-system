package ru.evgeny.asyncbookingsystem.rabbit;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class BookingEvent {

    private String messageId;
    private String requestId;
    private Long userId;
    private Long resourceId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
}
