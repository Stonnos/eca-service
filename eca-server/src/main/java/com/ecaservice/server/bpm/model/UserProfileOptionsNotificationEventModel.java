package com.ecaservice.server.bpm.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * User profile options notification event model for bpmn process.
 *
 * @author Roman Batygin
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileOptionsNotificationEventModel implements Serializable {

    /**
     * Email notifications enabled? (global flag)
     */
    private boolean emailEnabled;

    /**
     * Web push notifications enabled? (global flag)
     */
    private boolean webPushEnabled;
}
