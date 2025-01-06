package com.ecaservice.core.transactional.outbox.event.model;

import com.ecaservice.core.transactional.outbox.entity.OutboxMessageEntity;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.util.List;

/**
 * Outbox message saved event.
 *
 * @author Roman Batygin
 */
@Getter
public class OutboxMessageSavedEvent extends ApplicationEvent {

    /**
     * Outbox messages
     */
    private final List<OutboxMessageEntity> outboxMessages;

    /**
     * Constructor with parameters.
     *
     * @param source         - event initiator
     * @param outboxMessages - outbox messages
     */
    public OutboxMessageSavedEvent(Object source, List<OutboxMessageEntity> outboxMessages) {
        super(source);
        this.outboxMessages = outboxMessages;
    }
}
