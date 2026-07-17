package ru.evgeny.asyncbookingsystem.service;

import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import ru.evgeny.asyncbookingsystem.dto.CreateResourceRequest;
import ru.evgeny.asyncbookingsystem.dto.ResourceResponse;
import ru.evgeny.asyncbookingsystem.mapper.ResourceMapper;
import ru.evgeny.asyncbookingsystem.repository.ResourceRepository;

@Service
@RequiredArgsConstructor
public class ResourceService {

    private final ResourceRepository resourceRepository;
    private final ResourceMapper resourceMapper;

    @Transactional
    public ResourceResponse createResource(CreateResourceRequest request) {
        LocalDateTime now = LocalDateTime.now();
        return resourceMapper.toResponse(
                resourceRepository.save(resourceMapper.toEntity(request, now))
        );
    }

    @Transactional(readOnly = true)
    public List<ResourceResponse> getAllResources() {
        return resourceMapper.toResponses(resourceRepository.findAll());
    }

    @Transactional(readOnly = true)
    public ResourceResponse getResourceById(Long id) {
        return resourceRepository.findById(id)
                .map(resourceMapper::toResponse)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Resource with id " + id + " was not found"
                ));
    }
}
