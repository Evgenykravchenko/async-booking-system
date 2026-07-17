package ru.evgeny.asyncbookingsystem.repository;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.evgeny.asyncbookingsystem.entity.BookingEntity;

public interface BookingRepository extends JpaRepository<BookingEntity, Long> {

    List<BookingEntity> findAllByOrderByStartTimeAsc();

    List<BookingEntity> findAllByResource_IdOrderByStartTimeAsc(Long resourceId);

    long countByResource_IdAndStartTimeAndEndTime(Long resourceId, LocalDateTime startTime, LocalDateTime endTime);
}
