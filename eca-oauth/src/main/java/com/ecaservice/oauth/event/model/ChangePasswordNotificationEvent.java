package com.ecaservice.oauth.event.model;

import com.ecaservice.oauth.entity.ChangePasswordRequestEntity;
import lombok.Getter;

/**
 * Change password notification event.
 *
 * @author Roman Batygin
 */
public class ChangePasswordNotificationEvent extends AbstractNotificationEvent {

    /**
     * Change password request entity
     */
    @Getter
    private final ChangePasswordRequestEntity changePasswordRequestEntity;

    /**
     * Create a new {@code ChangePasswordNotificationEvent}.
     *
     * @param source                      - the object on which the event initially occurred or with which the event is
     *                                    associated (never {@code null})
     * @param changePasswordRequestEntity - change password request entity
     */
    public ChangePasswordNotificationEvent(Object source, ChangePasswordRequestEntity changePasswordRequestEntity) {
        super(source);
        this.changePasswordRequestEntity = changePasswordRequestEntity;
    }
}
