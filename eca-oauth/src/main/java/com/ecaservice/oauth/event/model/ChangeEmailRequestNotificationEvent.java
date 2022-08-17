package com.ecaservice.oauth.event.model;

import com.ecaservice.oauth.model.TokenModel;
import lombok.Getter;

/**
 * Change email notification event.
 *
 * @author Roman Batygin
 */
public class ChangeEmailRequestNotificationEvent extends AbstractTokenNotificationEvent {

    /**
     * New email
     */
    @Getter
    private final String newEmail;

    /**
     * Create a new {@code ChangeEmailNotificationEvent}.
     *
     * @param source     - the object on which the event initially occurred or with which the event is
     *                   associated (never {@code null})
     * @param tokenModel - change email request token model
     * @param newEmail   - new email
     */
    public ChangeEmailRequestNotificationEvent(Object source, TokenModel tokenModel, String newEmail) {
        super(source, tokenModel);
        this.newEmail = newEmail;
    }
}
