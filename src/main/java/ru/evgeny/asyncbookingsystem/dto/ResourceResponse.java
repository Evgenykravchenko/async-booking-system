package ru.evgeny.asyncbookingsystem.dto;

import java.time.LocalDateTime;

public record ResourceResponse(
        Long id,
        String name,
        String description,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
