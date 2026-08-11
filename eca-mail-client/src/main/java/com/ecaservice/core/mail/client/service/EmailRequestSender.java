package com.ecaservice.core.mail.client.service;

import com.ecaservice.notification.dto.EmailRequest;

/**
 * Email request sender interface.
 *
 * @author Roman Batygin
 */
public interface EmailRequestSender {

    /**
     * Sends email request.
     *
     * @param emailRequest - email request
     */
    void sendEmail(EmailRequest emailRequest);
}
