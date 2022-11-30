package com.ecaservice.core.mail.client.service;

import com.ecaservice.core.mail.client.event.model.AbstractEmailEvent;

/**
 * Email event processor interface.
 *
 * @author Roman Batygin
 */
public interface EmailEventProcessor {

    /**
     * Handles email event.
     *
     * @param event - email event.
     */
    void handleEmailEvent(AbstractEmailEvent event);
}
