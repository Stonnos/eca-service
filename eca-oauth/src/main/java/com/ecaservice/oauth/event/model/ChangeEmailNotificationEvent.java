package com.ecaservice.oauth.event.model;

import com.ecaservice.oauth.model.TokenModel;
import lombok.Getter;

/**
 * Change email notification event.
 *
 * @author Roman Batygin
 */
public class ChangeEmailNotificationEvent extends AbstractNotificationEvent {

    /**
     * Change email request token model
     */
    @Getter
    private final TokenModel tokenModel;
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
    public ChangeEmailNotificationEvent(Object source, TokenModel tokenModel, String newEmail) {
        super(source);
        this.tokenModel = tokenModel;
        this.newEmail = newEmail;
    }
}
