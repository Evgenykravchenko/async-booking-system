package ru.evgeny.asyncbookingsystem.service;

import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.evgeny.asyncbookingsystem.dto.BookingResponse;
import ru.evgeny.asyncbookingsystem.dto.CreateBookingRequest;
import ru.evgeny.asyncbookingsystem.entity.BookingStatus;
import ru.evgeny.asyncbookingsystem.entity.ResourceEntity;
import ru.evgeny.asyncbookingsystem.exception.SlotAlreadyBookedException;
import ru.evgeny.asyncbookingsystem.mapper.BookingMapper;
import ru.evgeny.asyncbookingsystem.repository.BookingRepository;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final ResourceService resourceService;
    private final BookingMapper bookingMapper;

    @Transactional
    public BookingResponse createBooking(CreateBookingRequest request) {
        ResourceEntity resource = resourceService.getResourceEntityById(request.resourceId());
        LocalDateTime now = LocalDateTime.now();

        try {
            return bookingMapper.toResponse(
                    bookingRepository.save(
                            bookingMapper.toEntity(request, resource, BookingStatus.BOOKED, now)
                    )
            );
        } catch (DataIntegrityViolationException exception) {
            throw new SlotAlreadyBookedException(request, exception);
        }
    }

    @Transactional(readOnly = true)
    public List<BookingResponse> getBookings(Long resourceId) {
        if (resourceId == null) {
            return bookingMapper.toResponses(bookingRepository.findAllByOrderByStartTimeAsc());
        }

        return bookingMapper.toResponses(bookingRepository.findAllByResource_IdOrderByStartTimeAsc(resourceId));
    }
}
