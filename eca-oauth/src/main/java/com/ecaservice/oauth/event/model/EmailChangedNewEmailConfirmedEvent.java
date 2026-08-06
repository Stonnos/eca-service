package com.ecaservice.oauth.event.model;

import com.ecaservice.oauth.entity.ChangeEmailRequestEntity;
import com.ecaservice.oauth.entity.UserEntity;
import lombok.Getter;

/**
 * Email changed new email confirmed event.
 *
 * @author Roman Batygin
 */
public class EmailChangedNewEmailConfirmedEvent extends AbstractUserEmailEvent {

    /**
     * Change email request entity
     */
    @Getter
    private final ChangeEmailRequestEntity changeEmailRequestEntity;

    /**
     * Create a new event.
     *
     * @param source                   - the object on which the event initially occurred or with which the event is
     *                                 associated (never {@code null})
     * @param userEntity               - user entity
     * @param changeEmailRequestEntity - change email request entity
     */
    public EmailChangedNewEmailConfirmedEvent(Object source, UserEntity userEntity,
                                              ChangeEmailRequestEntity changeEmailRequestEntity) {
        super(source, userEntity);
        this.changeEmailRequestEntity = changeEmailRequestEntity;
    }
}
