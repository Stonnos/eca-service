package com.ecaservice.web.push.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Message status
 *
 * @author Roman Batygin
 */
@Getter
@RequiredArgsConstructor
public enum MessageStatus {

    /**
     * Message read
     */
    READ("Прочитано"),

    /**
     * Message not read
     */
    NOT_READ("Не прочитано");

    private final String description;
}
