package com.ecaservice.core.push.client.service;

import com.ecaservice.web.push.dto.AbstractPushRequest;

/**
 * Web push sender interface.
 */
public interface WebPushSender {

    /**
     * Sends web push request
     *
     * @param pushRequest - push request
     */
    void send(AbstractPushRequest pushRequest);
}
