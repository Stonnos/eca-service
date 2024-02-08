package com.ecaservice.oauth.event.model;

import com.ecaservice.oauth.entity.UserEntity;
import lombok.Getter;

/**
 * Tfa code email event.
 *
 * @author Roman Batygin
 */
public class TfaCodeEmailEvent extends AbstractUserEmailEvent {

    /**
     * Tfa code
     */
    @Getter
    private final String tfaCode;

    /**
     * Create a new event.
     *
     * @param source     - the object on which the event initially occurred or with which the event is
     *                   associated (never {@code null})
     * @param userEntity - user entity
     * @param tfaCode    - tfa code
     */
    public TfaCodeEmailEvent(Object source, UserEntity userEntity, String tfaCode) {
        super(source, userEntity);
        this.tfaCode = tfaCode;
    }
}
