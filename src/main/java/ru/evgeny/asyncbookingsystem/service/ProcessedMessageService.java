package ru.evgeny.asyncbookingsystem.service;

import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.evgeny.asyncbookingsystem.entity.ProcessedMessageEntity;
import ru.evgeny.asyncbookingsystem.repository.ProcessedMessageRepository;

@Service
@RequiredArgsConstructor
public class ProcessedMessageService {

    private final ProcessedMessageRepository processedMessageRepository;

    @Transactional(readOnly = true)
    public boolean isProcessed(String messageId, String consumerName) {
        return processedMessageRepository.findByMessageIdAndConsumerName(messageId, consumerName).isPresent();
    }

    @Transactional
    public void markProcessed(String messageId, String consumerName) {
        processedMessageRepository.save(ProcessedMessageEntity.builder()
                .messageId(messageId)
                .consumerName(consumerName)
                .processedAt(LocalDateTime.now())
                .build());
    }
}
