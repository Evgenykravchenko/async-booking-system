package ru.evgeny.asyncbookingsystem.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.evgeny.asyncbookingsystem.entity.ProcessedMessageEntity;

public interface ProcessedMessageRepository extends JpaRepository<ProcessedMessageEntity, Long> {

    Optional<ProcessedMessageEntity> findByMessageIdAndConsumerName(String messageId, String consumerName);
}
