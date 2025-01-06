package com.ecaservice.core.transactional.outbox.model;

import lombok.Data;
import lombok.RequiredArgsConstructor;

/**
 * Outbox message.
 *
 * @author Roman Batygin
 */
@Data
@RequiredArgsConstructor
public class OutboxMessage {

    /**
     * Message code
     */
    private final String messageCode;

    /**
     * Message body
     */
    private final Object messageBody;
}
