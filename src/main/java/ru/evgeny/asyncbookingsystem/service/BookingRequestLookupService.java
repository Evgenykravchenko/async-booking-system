package ru.evgeny.asyncbookingsystem.service;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.evgeny.asyncbookingsystem.entity.BookingRequestEntity;
import ru.evgeny.asyncbookingsystem.repository.BookingRequestRepository;

@Service
@RequiredArgsConstructor
public class BookingRequestLookupService {

    private final BookingRequestRepository bookingRequestRepository;

    @Transactional(readOnly = true, propagation = Propagation.REQUIRES_NEW)
    public Optional<BookingRequestEntity> findByRequestId(String requestId) {
        return bookingRequestRepository.findByRequestId(requestId);
    }
}
