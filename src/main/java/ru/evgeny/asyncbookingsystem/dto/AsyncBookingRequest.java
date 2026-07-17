package ru.evgeny.asyncbookingsystem.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.LocalDateTime;

public record AsyncBookingRequest(
        @NotBlank(message = "Request id must not be blank")
        String requestId,
        @NotNull(message = "User id must not be null")
        @Positive(message = "User id must be positive")
        Long userId,
        @NotNull(message = "Resource id must not be null")
        @Positive(message = "Resource id must be positive")
        Long resourceId,
        @NotNull(message = "Start time must not be null")
        LocalDateTime startTime,
        @NotNull(message = "End time must not be null")
        LocalDateTime endTime
) {

    @AssertTrue(message = "Start time must be before end time")
    public boolean isTimeRangeValid() {
        if (startTime == null || endTime == null) {
            return true;
        }

        return startTime.isBefore(endTime);
    }
}
