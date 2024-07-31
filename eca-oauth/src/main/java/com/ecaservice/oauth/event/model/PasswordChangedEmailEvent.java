package com.ecaservice.oauth.event.model;

import com.ecaservice.oauth.entity.ChangePasswordRequestEntity;
import com.ecaservice.oauth.entity.UserEntity;
import lombok.Getter;

/**
 * Password changed email event.
 *
 * @author Roman Batygin
 */
public class PasswordChangedEmailEvent extends AbstractUserEmailEvent {

    /**
     * Change password request
     */
    @Getter
    private final ChangePasswordRequestEntity changePasswordRequest;

    /**
     * Create a new event.
     *
     * @param source                - the object on which the event initially occurred or with which the event is
     *                              associated (never {@code null})
     * @param userEntity            - user entity
     * @param changePasswordRequest - change password request entity
     */
    public PasswordChangedEmailEvent(Object source, UserEntity userEntity,
                                     ChangePasswordRequestEntity changePasswordRequest) {
        super(source, userEntity);
        this.changePasswordRequest = changePasswordRequest;
    }
}
