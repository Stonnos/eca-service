package com.ecaservice.core.message.template.model;

import lombok.Data;

/**
 * Message template entity.
 *
 * @author Roman Batygin
 */
@Data
public class MessageTemplate {

    /**
     * Template code
     */
    private String code;

    /**
     * Template text
     */
    private String templateText;
}
