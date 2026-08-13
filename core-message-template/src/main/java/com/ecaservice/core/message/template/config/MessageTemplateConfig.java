package com.ecaservice.core.message.template.config;

import com.ecaservice.core.message.template.model.MessageTemplate;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

/**
 * Message template config.
 *
 * @author Roman Batygin
 */
@Getter
@RequiredArgsConstructor
public class MessageTemplateConfig {

    private final List<MessageTemplate> templates;
}
