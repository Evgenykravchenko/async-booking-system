package ru.evgeny.asyncbookingsystem.mapper;

import java.time.LocalDateTime;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import ru.evgeny.asyncbookingsystem.dto.BookingResponse;
import ru.evgeny.asyncbookingsystem.dto.CreateBookingRequest;
import ru.evgeny.asyncbookingsystem.entity.BookingEntity;
import ru.evgeny.asyncbookingsystem.entity.BookingStatus;
import ru.evgeny.asyncbookingsystem.entity.ResourceEntity;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface BookingMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "resource", source = "resource")
    @Mapping(target = "userId", source = "request.userId")
    @Mapping(target = "startTime", source = "request.startTime")
    @Mapping(target = "endTime", source = "request.endTime")
    @Mapping(target = "status", source = "status")
    @Mapping(target = "createdAt", source = "timestamp")
    @Mapping(target = "updatedAt", source = "timestamp")
    BookingEntity toEntity(
            CreateBookingRequest request,
            ResourceEntity resource,
            BookingStatus status,
            LocalDateTime timestamp
    );

    @Mapping(target = "bookingId", source = "id")
    @Mapping(target = "resourceId", source = "resource.id")
    @Mapping(target = "status", source = "status", qualifiedByName = "mapBookingStatus")
    @Mapping(target = "reason", ignore = true)
    BookingResponse toResponse(BookingEntity booking);

    List<BookingResponse> toResponses(List<BookingEntity> bookings);

    @Named("mapBookingStatus")
    default String mapBookingStatus(BookingStatus status) {
        return status.name();
    }
}
