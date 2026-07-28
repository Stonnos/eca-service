package com.ecaservice.oauth.event.model;

import com.ecaservice.oauth.model.TokenModel;

/**
 * Force set password confirmation code email event.
 *
 * @author Roman Batygin
 */
public class ForceSetPasswordRequestConfirmationCodeEmailEvent extends AbstractTokenEmailEvent {

    /**
     * Create a new event.
     *
     * @param source     - the object on which the event initially occurred or with which the event is
     *                   associated (never {@code null})
     * @param tokenModel - token model
     */
    public ForceSetPasswordRequestConfirmationCodeEmailEvent(Object source, TokenModel tokenModel) {
        super(source, tokenModel);
    }
}
