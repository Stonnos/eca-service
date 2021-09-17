package com.ecaservice.oauth.event.model;

import com.ecaservice.oauth.model.TokenModel;

/**
 * Change password notification event.
 *
 * @author Roman Batygin
 */
public class ChangePasswordRequestNotificationEvent extends AbstractTokenNotificationEvent {

    /**
     * Create a new {@code ChangePasswordNotificationEvent}.
     *
     * @param source     - the object on which the event initially occurred or with which the event is
     *                   associated (never {@code null})
     * @param tokenModel - change password request token model
     */
    public ChangePasswordRequestNotificationEvent(Object source, TokenModel tokenModel) {
        super(source, tokenModel);
    }
}
