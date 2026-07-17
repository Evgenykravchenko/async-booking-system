package ru.evgeny.asyncbookingsystem.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ConcurrentBookingTestResponse {

    private int totalRequests;
    private int success;
    private int rejected;
    private int duplicates;
}
