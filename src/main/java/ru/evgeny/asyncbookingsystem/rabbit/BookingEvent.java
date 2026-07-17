package ru.evgeny.asyncbookingsystem.rabbit;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingEvent {

    private String messageId;
    private String requestId;
    private Long userId;
    private Long resourceId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
}
