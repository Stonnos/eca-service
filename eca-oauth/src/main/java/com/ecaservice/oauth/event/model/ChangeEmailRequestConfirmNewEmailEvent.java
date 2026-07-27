package com.ecaservice.oauth.event.model;

import com.ecaservice.oauth.model.TokenModel;
import lombok.Getter;

/**
 * Change email request confirm new event.
 *
 * @author Roman Batygin
 */
public class ChangeEmailRequestConfirmNewEmailEvent extends AbstractTokenEmailEvent {

    /**
     * New email
     */
    @Getter
    private final String newEmail;

    /**
     * Create a new event.
     *
     * @param source     - the object on which the event initially occurred or with which the event is
     *                   associated (never {@code null})
     * @param tokenModel - change email request token model
     * @param newEmail   - new email
     */
    public ChangeEmailRequestConfirmNewEmailEvent(Object source, TokenModel tokenModel, String newEmail) {
        super(source, tokenModel);
        this.newEmail = newEmail;
    }
}
