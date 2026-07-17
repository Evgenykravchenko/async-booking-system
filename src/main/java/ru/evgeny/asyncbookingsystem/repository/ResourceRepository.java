package ru.evgeny.asyncbookingsystem.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.evgeny.asyncbookingsystem.entity.ResourceEntity;

public interface ResourceRepository extends JpaRepository<ResourceEntity, Long> {
}
