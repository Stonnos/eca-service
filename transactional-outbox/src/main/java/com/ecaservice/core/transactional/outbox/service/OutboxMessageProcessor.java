package com.ecaservice.core.transactional.outbox.service;

import com.ecaservice.core.transactional.outbox.config.TransactionalOutboxProperties;
import com.ecaservice.core.transactional.outbox.entity.OutboxMessageEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Implements scheduler for sent outbox messages.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OutboxMessageProcessor {

    private final TransactionalOutboxProperties transactionalOutboxProperties;
    private final OutboxMessageService outboxMessageService;
    private final OutboxSenderService outboxSenderService;

    /**
     * Processes not sent outbox messages.
     */
    public void processNotSentMessages() {
        log.debug("Starting outbox messages job");
        var pageRequest = PageRequest.of(0, transactionalOutboxProperties.getPageSize());
        List<OutboxMessageEntity> outboxMessages;
        while (!(outboxMessages = outboxMessageService.lockNextNotSentMessages(pageRequest)).isEmpty()) {
            outboxMessages.forEach(outboxSenderService::sentOutboxMessage);
        }
        log.debug("Outbox messages has been finished");
    }
}
