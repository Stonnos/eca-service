package com.ecaservice.oauth.event.model;

import com.ecaservice.oauth.model.TokenModel;

/**
 * Reset password email event.
 *
 * @author Roman Batygin
 */
public class ResetPasswordRequestEmailEvent extends AbstractTokenEmailEvent {

    /**
     * Create a new event.
     *
     * @param source     - the object on which the event initially occurred or with which the event is
     *                   associated (never {@code null})
     * @param tokenModel - reset password request model
     */
    public ResetPasswordRequestEmailEvent(Object source, TokenModel tokenModel) {
        super(source, tokenModel);
    }
}
