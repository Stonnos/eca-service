package com.ecaservice.oauth.event.model;

import com.ecaservice.oauth.entity.ResetPasswordRequestEntity;
import lombok.Getter;

/**
 * Reset password notification event.
 *
 * @author Roman Batygin
 */
public class ResetPasswordNotificationEvent extends AbstractNotificationEvent {

    /**
     * User entity
     */
    @Getter
    private final ResetPasswordRequestEntity resetPasswordRequestEntity;

    /**
     * Create a new {@code ResetPasswordNotificationEvent}.
     *
     * @param source                     - the object on which the event initially occurred or with which the event is
     *                                   associated (never {@code null})
     * @param resetPasswordRequestEntity - reset password request created entity
     */
    public ResetPasswordNotificationEvent(Object source, ResetPasswordRequestEntity resetPasswordRequestEntity) {
        super(source);
        this.resetPasswordRequestEntity = resetPasswordRequestEntity;
    }
}
