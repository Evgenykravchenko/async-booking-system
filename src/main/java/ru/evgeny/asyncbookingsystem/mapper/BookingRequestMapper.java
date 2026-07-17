package ru.evgeny.asyncbookingsystem.mapper;

import java.time.LocalDateTime;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import ru.evgeny.asyncbookingsystem.dto.AsyncBookingRequest;
import ru.evgeny.asyncbookingsystem.dto.BookingRequestResponse;
import ru.evgeny.asyncbookingsystem.entity.BookingRequestEntity;
import ru.evgeny.asyncbookingsystem.entity.BookingRequestStatus;
import ru.evgeny.asyncbookingsystem.entity.ResourceEntity;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface BookingRequestMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "requestId", source = "request.requestId", qualifiedByName = "trimRequestId")
    @Mapping(target = "resource", source = "resource")
    @Mapping(target = "userId", source = "request.userId")
    @Mapping(target = "startTime", source = "request.startTime")
    @Mapping(target = "endTime", source = "request.endTime")
    @Mapping(target = "status", source = "status")
    @Mapping(target = "failureReason", ignore = true)
    @Mapping(target = "createdAt", source = "timestamp")
    @Mapping(target = "updatedAt", source = "timestamp")
    BookingRequestEntity toEntity(
            AsyncBookingRequest request,
            ResourceEntity resource,
            BookingRequestStatus status,
            LocalDateTime timestamp
    );

    @Mapping(target = "resourceId", source = "resource.id")
    @Mapping(target = "status", source = "status", qualifiedByName = "mapStatus")
    @Mapping(target = "reason", source = "failureReason")
    BookingRequestResponse toResponse(BookingRequestEntity bookingRequest);

    @Named("trimRequestId")
    default String trimRequestId(String requestId) {
        return requestId.trim();
    }

    @Named("mapStatus")
    default String mapStatus(BookingRequestStatus status) {
        return status.name();
    }
}
