package ru.evgeny.asyncbookingsystem.service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.evgeny.asyncbookingsystem.dto.ConcurrentBookingTestRequest;
import ru.evgeny.asyncbookingsystem.dto.ConcurrentBookingTestResponse;
import ru.evgeny.asyncbookingsystem.dto.CreateBookingRequest;
import ru.evgeny.asyncbookingsystem.exception.SlotAlreadyBookedException;
import ru.evgeny.asyncbookingsystem.repository.BookingRepository;

@Service
@RequiredArgsConstructor
public class ConcurrentBookingTestService {

    private final BookingService bookingService;
    private final BookingRepository bookingRepository;

    public ConcurrentBookingTestResponse runConcurrentBookingTest(ConcurrentBookingTestRequest request) {
        int threads = request.threads();
        ExecutorService executorService = Executors.newFixedThreadPool(threads);
        CountDownLatch readyLatch = new CountDownLatch(threads);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch completionLatch = new CountDownLatch(threads);

        try {
            List<Future<AttemptResult>> futures = submitTasks(
                    executorService,
                    buildTasks(request, readyLatch, startLatch, completionLatch)
            );

            awaitLatch(readyLatch);
            startLatch.countDown();
            awaitLatch(completionLatch);

            return buildResponse(request, futures);
        } finally {
            executorService.shutdownNow();
        }
    }

    private List<Callable<AttemptResult>> buildTasks(
            ConcurrentBookingTestRequest request,
            CountDownLatch readyLatch,
            CountDownLatch startLatch,
            CountDownLatch completionLatch
    ) {
        List<Callable<AttemptResult>> tasks = new ArrayList<>();

        for (int index = 0; index < request.threads(); index++) {
            long userId = index + 1L;
            tasks.add(() -> executeAttempt(request, userId, readyLatch, startLatch, completionLatch));
        }

        return tasks;
    }

    private List<Future<AttemptResult>> submitTasks(
            ExecutorService executorService,
            List<Callable<AttemptResult>> tasks
    ) {
        List<Future<AttemptResult>> futures = new ArrayList<>(tasks.size());

        for (Callable<AttemptResult> task : tasks) {
            futures.add(executorService.submit(task));
        }

        return futures;
    }

    private AttemptResult executeAttempt(
            ConcurrentBookingTestRequest request,
            Long userId,
            CountDownLatch readyLatch,
            CountDownLatch startLatch,
            CountDownLatch completionLatch
    ) {
        readyLatch.countDown();

        try {
            awaitLatch(startLatch);
            bookingService.createBooking(new CreateBookingRequest(
                    userId,
                    request.resourceId(),
                    request.startTime(),
                    request.endTime()
            ));
            return AttemptResult.SUCCESS;
        } catch (SlotAlreadyBookedException exception) {
            return AttemptResult.REJECTED;
        } finally {
            completionLatch.countDown();
        }
    }

    private ConcurrentBookingTestResponse buildResponse(
            ConcurrentBookingTestRequest request,
            List<Future<AttemptResult>> futures
    ) {
        int success = 0;
        int rejected = 0;

        for (Future<AttemptResult> future : futures) {
            AttemptResult result = getFutureResult(future);
            if (result == AttemptResult.SUCCESS) {
                success++;
            } else {
                rejected++;
            }
        }

        long persistedBookings = countPersistedBookings(request);
        int duplicates = Math.max((int) persistedBookings - 1, 0);

        return ConcurrentBookingTestResponse.builder()
                .totalRequests(request.threads())
                .success(success)
                .rejected(rejected)
                .duplicates(duplicates)
                .build();
    }

    @Transactional(readOnly = true)
    protected long countPersistedBookings(ConcurrentBookingTestRequest request) {
        return bookingRepository.countByResource_IdAndStartTimeAndEndTime(
                request.resourceId(),
                request.startTime(),
                request.endTime()
        );
    }

    private AttemptResult getFutureResult(Future<AttemptResult> future) {
        try {
            return future.get();
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Concurrent booking test was interrupted", exception);
        } catch (ExecutionException exception) {
            throw new IllegalStateException("Concurrent booking test failed", exception.getCause());
        }
    }

    private void awaitLatch(CountDownLatch latch) {
        try {
            latch.await();
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Concurrent booking test was interrupted", exception);
        }
    }

    private enum AttemptResult {
        SUCCESS,
        REJECTED
    }
}
