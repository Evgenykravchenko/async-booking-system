package ru.evgeny.asyncbookingsystem.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.evgeny.asyncbookingsystem.dto.ConcurrentBookingTestRequest;
import ru.evgeny.asyncbookingsystem.dto.ConcurrentBookingTestResponse;
import ru.evgeny.asyncbookingsystem.service.ConcurrentBookingTestService;

@RestController
@RequestMapping("/api/test")
@RequiredArgsConstructor
public class TestController {

    private final ConcurrentBookingTestService concurrentBookingTestService;

    @PostMapping("/concurrent-booking")
    public ConcurrentBookingTestResponse runConcurrentBookingTest(
            @Valid @RequestBody ConcurrentBookingTestRequest request
    ) {
        return concurrentBookingTestService.runConcurrentBookingTest(request);
    }
}
