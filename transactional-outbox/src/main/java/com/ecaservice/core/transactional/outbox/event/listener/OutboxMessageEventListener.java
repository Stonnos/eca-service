package com.ecaservice.core.transactional.outbox.event.listener;

import com.ecaservice.core.transactional.outbox.entity.OutboxMessageEntity;
import com.ecaservice.core.transactional.outbox.event.model.OutboxMessageSavedEvent;
import com.ecaservice.core.transactional.outbox.service.OutboxMessageProcessor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * Outbox message event listener.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OutboxMessageEventListener {

    private final OutboxMessageProcessor outboxMessageProcessor;

    /**
     * Handles outbox message saved event.
     *
     * @param event - outbox message saved event
     */
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, classes = OutboxMessageSavedEvent.class)
    public void handleEvent(OutboxMessageSavedEvent event) {
        var ids = event.getOutboxMessages().stream().map(OutboxMessageEntity::getId).toList();
        log.info("Starting to handle outbox messages [{}] committed from [{}]", ids, event.getSource());
        outboxMessageProcessor.processNotSentMessages();
        log.info("Outbox message [{}] event committed from [{}] has been processed", ids, event.getSource());
    }
}
