package com.ecaservice.core.message.template.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Message templates properties.
 *
 * @author Roman Batygin
 */
@Data
@ConfigurationProperties("message-templates")
public class MessageTemplateProperties {

    private static final String MESSAGE_TEMPLATES_JSON = "message-templates/message-templates.json";

    /**
     * Templates location
     */
    private String location = MESSAGE_TEMPLATES_JSON;
}
