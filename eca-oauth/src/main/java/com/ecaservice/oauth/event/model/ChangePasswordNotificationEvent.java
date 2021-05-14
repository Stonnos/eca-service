package com.ecaservice.oauth.event.model;

import com.ecaservice.oauth.model.TokenModel;
import lombok.Getter;

/**
 * Change password notification event.
 *
 * @author Roman Batygin
 */
public class ChangePasswordNotificationEvent extends AbstractNotificationEvent {

    /**
     * Change password request token model
     */
    @Getter
    private final TokenModel tokenModel;

    /**
     * Create a new {@code ChangePasswordNotificationEvent}.
     *
     * @param source     - the object on which the event initially occurred or with which the event is
     *                   associated (never {@code null})
     * @param tokenModel - change password request token model
     */
    public ChangePasswordNotificationEvent(Object source, TokenModel tokenModel) {
        super(source);
        this.tokenModel = tokenModel;
    }
}
