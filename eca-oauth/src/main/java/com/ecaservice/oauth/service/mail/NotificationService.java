package com.ecaservice.oauth.service.mail;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Implements service for notifications sending.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final EmailClient emailClient;

    /**
     * Send email notification with created user details.
     *
     * @param login    - user login
     * @param password - user password
     */
    public void notifyUserCreated(String login, String password) {

    }
}
