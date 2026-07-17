package ru.evgeny.asyncbookingsystem.controller;

import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.evgeny.asyncbookingsystem.dto.CreateResourceRequest;
import ru.evgeny.asyncbookingsystem.dto.ResourceResponse;
import ru.evgeny.asyncbookingsystem.service.ResourceService;

@RestController
@RequestMapping("/api/resources")
@RequiredArgsConstructor
public class ResourceController {

    private final ResourceService resourceService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResourceResponse createResource(@Valid @RequestBody CreateResourceRequest request) {
        return resourceService.createResource(request);
    }

    @GetMapping
    public List<ResourceResponse> getAllResources() {
        return resourceService.getAllResources();
    }

    @GetMapping("/{id}")
    public ResourceResponse getResourceById(@PathVariable Long id) {
        return resourceService.getResourceById(id);
    }
}
