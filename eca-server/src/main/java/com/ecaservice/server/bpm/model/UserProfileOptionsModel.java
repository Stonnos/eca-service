package com.ecaservice.server.bpm.model;

import lombok.Data;

import java.io.Serializable;
import java.util.Map;

import static com.google.common.collect.Maps.newHashMap;

/**
 * User profile options model for bpmn process.
 *
 * @author Roman Batygin
 */
@Data
public class UserProfileOptionsModel implements Serializable {

    /**
     * Email notifications enabled? (global flag)
     */
    private boolean emailEnabled;

    /**
     * Web push notifications enabled? (global flag)
     */
    private boolean webPushEnabled;

    /**
     * Notification event options
     */
    private Map<String, UserProfileOptionsNotificationEventModel> notificationEventOptions = newHashMap();
}
