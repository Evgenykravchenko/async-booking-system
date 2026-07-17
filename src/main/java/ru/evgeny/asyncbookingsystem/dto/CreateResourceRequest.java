package ru.evgeny.asyncbookingsystem.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateResourceRequest(
        @NotBlank(message = "Resource name must not be blank")
        @Size(max = 255, message = "Resource name must be at most 255 characters")
        String name,
        @Size(max = 5000, message = "Resource description must be at most 5000 characters")
        String description
) {
}
