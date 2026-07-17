package ru.evgeny.asyncbookingsystem.service;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import ru.evgeny.asyncbookingsystem.dto.AsyncBookingRequest;
import ru.evgeny.asyncbookingsystem.dto.BookingRequestResponse;
import ru.evgeny.asyncbookingsystem.entity.BookingRequestEntity;
import ru.evgeny.asyncbookingsystem.entity.BookingRequestStatus;
import ru.evgeny.asyncbookingsystem.entity.ResourceEntity;
import ru.evgeny.asyncbookingsystem.exception.BookingRequestNotFoundException;
import ru.evgeny.asyncbookingsystem.exception.BookingRequestPayloadMismatchException;
import ru.evgeny.asyncbookingsystem.mapper.BookingRequestMapper;
import ru.evgeny.asyncbookingsystem.rabbit.BookingEvent;
import ru.evgeny.asyncbookingsystem.rabbit.BookingEventProducer;
import ru.evgeny.asyncbookingsystem.repository.BookingRequestRepository;

@Service
@RequiredArgsConstructor
public class BookingRequestService {

    private final BookingRequestRepository bookingRequestRepository;
    private final BookingRequestLookupService bookingRequestLookupService;
    private final ResourceService resourceService;
    private final BookingRequestMapper bookingRequestMapper;
    private final BookingEventProducer bookingEventProducer;

    @Transactional
    public BookingRequestResponse createBookingRequest(AsyncBookingRequest request) {
        String requestId = request.requestId().trim();

        return bookingRequestRepository.findByRequestId(requestId)
                .map(existingRequest -> resolveExistingRequest(existingRequest, request, requestId))
                .orElseGet(() -> createNewBookingRequest(request, requestId));
    }

    @Transactional(readOnly = true)
    public BookingRequestResponse getByRequestId(String requestId) {
        return bookingRequestRepository.findByRequestId(requestId.trim())
                .map(bookingRequestMapper::toResponse)
                .orElseThrow(() -> new BookingRequestNotFoundException(requestId));
    }

    private BookingRequestResponse createNewBookingRequest(AsyncBookingRequest request, String requestId) {
        ResourceEntity resource = resourceService.getResourceEntityById(request.resourceId());
        LocalDateTime now = LocalDateTime.now();

        try {
            BookingRequestEntity savedBookingRequest = bookingRequestRepository.save(
                    bookingRequestMapper.toEntity(request, resource, BookingRequestStatus.PENDING, now)
            );
            publishBookingRequestedEvent(savedBookingRequest);

            return bookingRequestMapper.toResponse(savedBookingRequest);
        } catch (DataIntegrityViolationException exception) {
            return bookingRequestLookupService.findByRequestId(requestId)
                    .map(existingRequest -> resolveExistingRequest(existingRequest, request, requestId))
                    .orElseThrow(() -> exception);
        }
    }

    private BookingRequestResponse resolveExistingRequest(
            BookingRequestEntity existingRequest,
            AsyncBookingRequest request,
            String requestId
    ) {
        if (!isSamePayload(existingRequest, request)) {
            throw new BookingRequestPayloadMismatchException(requestId);
        }

        return bookingRequestMapper.toResponse(existingRequest);
    }

    private boolean isSamePayload(BookingRequestEntity existingRequest, AsyncBookingRequest request) {
        return existingRequest.getUserId().equals(request.userId())
                && existingRequest.getResource().getId().equals(request.resourceId())
                && existingRequest.getStartTime().equals(request.startTime())
                && existingRequest.getEndTime().equals(request.endTime());
    }

    private void publishBookingRequestedEvent(BookingRequestEntity bookingRequest) {
        BookingEvent bookingEvent = BookingEvent.builder()
                .messageId(UUID.randomUUID().toString())
                .requestId(bookingRequest.getRequestId())
                .userId(bookingRequest.getUserId())
                .resourceId(bookingRequest.getResource().getId())
                .startTime(bookingRequest.getStartTime())
                .endTime(bookingRequest.getEndTime())
                .build();

        if (!TransactionSynchronizationManager.isActualTransactionActive()) {
            bookingEventProducer.publishBookingRequested(bookingEvent);
            return;
        }

        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                bookingEventProducer.publishBookingRequested(bookingEvent);
            }
        });
    }
}
