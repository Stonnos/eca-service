package com.ecaservice.oauth.event.model;

import com.ecaservice.oauth.model.TokenModel;

/**
 * Change password email event.
 *
 * @author Roman Batygin
 */
public class ChangePasswordRequestEmailEvent extends AbstractTokenEmailEvent {

    /**
     * Create a new event.
     *
     * @param source     - the object on which the event initially occurred or with which the event is
     *                   associated (never {@code null})
     * @param tokenModel - change password request token model
     */
    public ChangePasswordRequestEmailEvent(Object source, TokenModel tokenModel) {
        super(source, tokenModel);
    }
}
