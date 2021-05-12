package com.ecaservice.oauth.event.model;

import com.ecaservice.oauth.model.TokenModel;
import lombok.Getter;

/**
 * Reset password notification event.
 *
 * @author Roman Batygin
 */
public class ResetPasswordNotificationEvent extends AbstractNotificationEvent {

    /**
     * Reset password request model
     */
    @Getter
    private final TokenModel tokenModel;

    /**
     * Create a new {@code ResetPasswordNotificationEvent}.
     *
     * @param source     - the object on which the event initially occurred or with which the event is
     *                   associated (never {@code null})
     * @param tokenModel - reset password request model
     */
    public ResetPasswordNotificationEvent(Object source, TokenModel tokenModel) {
        super(source);
        this.tokenModel = tokenModel;
    }
}
