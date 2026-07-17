package ru.evgeny.asyncbookingsystem.service;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.evgeny.asyncbookingsystem.dto.AsyncBookingRequest;
import ru.evgeny.asyncbookingsystem.dto.BookingRequestResponse;
import ru.evgeny.asyncbookingsystem.entity.BookingRequestEntity;
import ru.evgeny.asyncbookingsystem.entity.BookingRequestStatus;
import ru.evgeny.asyncbookingsystem.entity.ResourceEntity;
import ru.evgeny.asyncbookingsystem.exception.BookingRequestNotFoundException;
import ru.evgeny.asyncbookingsystem.mapper.BookingRequestMapper;
import ru.evgeny.asyncbookingsystem.rabbit.BookingEvent;
import ru.evgeny.asyncbookingsystem.rabbit.BookingEventProducer;
import ru.evgeny.asyncbookingsystem.repository.BookingRequestRepository;

@Service
@RequiredArgsConstructor
public class BookingRequestService {

    private final BookingRequestRepository bookingRequestRepository;
    private final ResourceService resourceService;
    private final BookingRequestMapper bookingRequestMapper;
    private final BookingEventProducer bookingEventProducer;

    @Transactional
    public BookingRequestResponse createBookingRequest(AsyncBookingRequest request) {
        String requestId = request.requestId().trim();

        return bookingRequestRepository.findByRequestId(requestId)
                .map(bookingRequestMapper::toResponse)
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
            return bookingRequestRepository.findByRequestId(requestId)
                    .map(bookingRequestMapper::toResponse)
                    .orElseThrow(() -> exception);
        }
    }

    private void publishBookingRequestedEvent(BookingRequestEntity bookingRequest) {
        bookingEventProducer.publishBookingRequested(
                BookingEvent.builder()
                        .messageId(UUID.randomUUID().toString())
                        .requestId(bookingRequest.getRequestId())
                        .userId(bookingRequest.getUserId())
                        .resourceId(bookingRequest.getResource().getId())
                        .startTime(bookingRequest.getStartTime())
                        .endTime(bookingRequest.getEndTime())
                        .build()
        );
    }
}
