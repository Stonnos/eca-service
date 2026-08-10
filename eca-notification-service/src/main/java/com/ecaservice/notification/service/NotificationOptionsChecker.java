package com.ecaservice.notification.service;

/**
 * Notification options checker.
 *
 * @author Roman Batygin
 */
public interface NotificationOptionsChecker {


    /**
     * Is notification enabled for specified request.
     *
     * @param user        - user login
     * @param messageCode - message code
     * @return {@code true} if notification is enabled, otherwise {@code false}
     */
    boolean isEnabled(String user, String messageCode);
}
