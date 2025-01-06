package com.ecaservice.core.transactional.outbox.service;

import com.ecaservice.core.transactional.outbox.config.TransactionalOutboxProperties;
import com.ecaservice.core.transactional.outbox.entity.OutboxMessageEntity;
import com.ecaservice.core.transactional.outbox.event.model.OutboxMessageSavedEvent;
import com.ecaservice.core.transactional.outbox.model.OutboxMessage;
import com.ecaservice.core.transactional.outbox.repository.OutboxMessageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static com.ecaservice.common.web.util.JsonUtils.toJson;

/**
 * Implements outbox message service.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OutboxMessageService {

    private final TransactionalOutboxProperties transactionalOutboxProperties;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final OutboxMessageRepository outboxMessageRepository;

    /**
     * Saves outbox messages into db.
     *
     * @param outboxMessages - outbox messages
     */
    @Transactional(propagation = Propagation.MANDATORY)
    public void saveOutboxMessage(List<OutboxMessage> outboxMessages) {
        log.info("Starting to save outbox messages [{}]", outboxMessages.size());
        List<OutboxMessageEntity> outboxMessageEntities = outboxMessages.stream()
                .map(outboxMessage -> {
                    OutboxMessageEntity outboxMessageEntity = new OutboxMessageEntity();
                    outboxMessageEntity.setMessageCode(outboxMessage.getMessageCode());
                    outboxMessageEntity.setMessageBody(toJson(outboxMessage.getMessageBody()));
                    outboxMessageEntity.setCreatedAt(LocalDateTime.now());
                    return outboxMessageEntity;
                })
                .toList();
        outboxMessageRepository.saveAll(outboxMessageEntities);
        log.info("Outbox messages ids [{}] has been saved",
                outboxMessageEntities.stream()
                        .map(OutboxMessageEntity::getId)
                        .collect(Collectors.toList())
        );
        applicationEventPublisher.publishEvent(new OutboxMessageSavedEvent(this, outboxMessageEntities));
    }

    /**
     * Deletes outbox message from db.
     *
     * @param outboxMessageEntity -outbox message entity
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void delete(OutboxMessageEntity outboxMessageEntity) {
        log.info("Starting to delete outbox message [{}] with id [{}]", outboxMessageEntity.getMessageCode(),
                outboxMessageEntity.getId());
        outboxMessageRepository.delete(outboxMessageEntity);
        log.info("Outbox message [{}] with id [{}] has been removed", outboxMessageEntity.getMessageCode(),
                outboxMessageEntity.getId());
    }

    /**
     * Gets next outbox messages for processing and sets a lock to prevent other threads from receiving the same data for
     * processing.
     *
     * @param pageable - pageable object
     * @return outbox messages list
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public List<OutboxMessageEntity> lockNextNotSentMessages(Pageable pageable) {
        log.debug("Starting to get next outbox messages to process");
        List<OutboxMessageEntity> outboxMessages =
                outboxMessageRepository.getNotSentMessages(LocalDateTime.now(), pageable);
        if (!CollectionUtils.isEmpty(outboxMessages)) {
            // Sets a lock to prevent other threads from receiving the same data for processing
            LocalDateTime lockedTtl =
                    LocalDateTime.now().plusSeconds(transactionalOutboxProperties.getLockTtlSeconds());
            var ids = outboxMessages.stream()
                    .map(OutboxMessageEntity::getId)
                    .toList();
            outboxMessageRepository.lock(ids, lockedTtl);
            log.info("[{}] outbox messages to process has been fetched", outboxMessages.size());
        }
        log.debug("Next outbox messages to process has been fetched");
        return outboxMessages;
    }
}
