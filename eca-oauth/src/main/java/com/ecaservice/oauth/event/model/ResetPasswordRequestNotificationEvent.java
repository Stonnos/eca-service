package com.ecaservice.oauth.event.model;

import com.ecaservice.oauth.model.TokenModel;

/**
 * Reset password notification event.
 *
 * @author Roman Batygin
 */
public class ResetPasswordRequestNotificationEvent extends AbstractTokenNotificationEvent {

    /**
     * Create a new {@code ResetPasswordNotificationEvent}.
     *
     * @param source     - the object on which the event initially occurred or with which the event is
     *                   associated (never {@code null})
     * @param tokenModel - reset password request model
     */
    public ResetPasswordRequestNotificationEvent(Object source, TokenModel tokenModel) {
        super(source, tokenModel);
    }
}
