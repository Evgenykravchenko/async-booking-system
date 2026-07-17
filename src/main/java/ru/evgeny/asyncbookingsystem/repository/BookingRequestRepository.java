package ru.evgeny.asyncbookingsystem.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.evgeny.asyncbookingsystem.entity.BookingRequestEntity;

public interface BookingRequestRepository extends JpaRepository<BookingRequestEntity, Long> {

    Optional<BookingRequestEntity> findByRequestId(String requestId);
}
