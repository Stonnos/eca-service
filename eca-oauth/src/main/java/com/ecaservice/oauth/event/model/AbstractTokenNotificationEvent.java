package com.ecaservice.oauth.event.model;

import com.ecaservice.oauth.model.TokenModel;
import lombok.Getter;

/**
 * Abstract token notification event.
 *
 * @author Roman Batygin
 */
public abstract class AbstractTokenNotificationEvent extends AbstractNotificationEvent {

    /**
     * Token model
     */
    @Getter
    private final TokenModel tokenModel;

    /**
     * Create a new {@code AbstractTokenNotificationEvent}.
     *
     * @param source     the object on which the event initially occurred or with
     *                   which the event is associated (never {@code null})
     * @param tokenModel - token model
     */
    protected AbstractTokenNotificationEvent(Object source, TokenModel tokenModel) {
        super(source);
        this.tokenModel = tokenModel;
    }

    @Override
    public String getReceiver() {
        return tokenModel.getEmail();
    }
}
