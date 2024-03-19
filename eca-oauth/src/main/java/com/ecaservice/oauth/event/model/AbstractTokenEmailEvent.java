package com.ecaservice.oauth.event.model;

import com.ecaservice.core.mail.client.event.model.AbstractEmailEvent;
import com.ecaservice.oauth.model.TokenModel;
import lombok.Getter;

/**
 * Abstract token email event.
 *
 * @author Roman Batygin
 */
public abstract class AbstractTokenEmailEvent extends AbstractEmailEvent {

    /**
     * Token model
     */
    @Getter
    private final TokenModel tokenModel;

    /**
     * Create a new event.
     *
     * @param source     the object on which the event initially occurred or with
     *                   which the event is associated (never {@code null})
     * @param tokenModel - token model
     */
    protected AbstractTokenEmailEvent(Object source, TokenModel tokenModel) {
        super(source);
        this.tokenModel = tokenModel;
    }
}
