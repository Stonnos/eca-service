package com.ecaservice.oauth.event.model;

import com.ecaservice.oauth.model.TokenModel;
import lombok.Getter;

/**
 * Change email email event.
 *
 * @author Roman Batygin
 */
public class ChangeEmailRequestEmailEvent extends AbstractTokenEmailEvent {

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
    public ChangeEmailRequestEmailEvent(Object source, TokenModel tokenModel, String newEmail) {
        super(source, tokenModel);
        this.newEmail = newEmail;
    }
}
