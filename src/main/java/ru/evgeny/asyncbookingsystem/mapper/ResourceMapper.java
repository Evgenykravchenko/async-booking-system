package ru.evgeny.asyncbookingsystem.mapper;

import java.time.LocalDateTime;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import ru.evgeny.asyncbookingsystem.dto.CreateResourceRequest;
import ru.evgeny.asyncbookingsystem.dto.ResourceResponse;
import ru.evgeny.asyncbookingsystem.entity.ResourceEntity;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface ResourceMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "name", source = "request.name", qualifiedByName = "trimRequiredText")
    @Mapping(target = "description", source = "request.description", qualifiedByName = "normalizeOptionalText")
    @Mapping(target = "createdAt", source = "timestamp")
    @Mapping(target = "updatedAt", source = "timestamp")
    ResourceEntity toEntity(CreateResourceRequest request, LocalDateTime timestamp);

    ResourceResponse toResponse(ResourceEntity resource);

    List<ResourceResponse> toResponses(List<ResourceEntity> resources);

    @Named("trimRequiredText")
    default String trimRequiredText(String value) {
        return value.trim();
    }

    @Named("normalizeOptionalText")
    default String normalizeOptionalText(String value) {
        if (value == null) {
            return null;
        }

        String trimmedValue = value.trim();
        return trimmedValue.isEmpty() ? null : trimmedValue;
    }
}
