package com.ecaservice.oauth.event.model;

import com.ecaservice.oauth.entity.ResetPasswordRequestEntity;
import lombok.Getter;

/**
 * Reset password request created event.
 *
 * @author Roman Batygin
 */
public class ResetPasswordRequestCreatedEvent extends AbstractNotificationEvent {

    /**
     * User entity
     */
    @Getter
    private final ResetPasswordRequestEntity resetPasswordRequestEntity;

    /**
     * Create a new {@code ResetPasswordRequestCreatedEvent}.
     *
     * @param source                     - the object on which the event initially occurred or with which the event is
     *                                   associated (never {@code null})
     * @param resetPasswordRequestEntity - reset password request created entity
     */
    public ResetPasswordRequestCreatedEvent(Object source, ResetPasswordRequestEntity resetPasswordRequestEntity) {
        super(source);
        this.resetPasswordRequestEntity = resetPasswordRequestEntity;
    }
}
