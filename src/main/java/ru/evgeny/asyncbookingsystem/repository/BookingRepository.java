package ru.evgeny.asyncbookingsystem.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.evgeny.asyncbookingsystem.entity.BookingEntity;

public interface BookingRepository extends JpaRepository<BookingEntity, Long> {

    List<BookingEntity> findAllByOrderByStartTimeAsc();

    List<BookingEntity> findAllByResource_IdOrderByStartTimeAsc(Long resourceId);
}
