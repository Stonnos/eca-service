package com.ecaservice.server.service.message.template;

import java.util.Map;

/**
 * Message template processor interface.
 *
 * @author Roman Batygin
 */
public interface MessageTemplateProcessor {

    /**
     * Processed message template based on input parameters.
     *
     * @param templateCode - template code
     * @param variables    - template variables map
     * @return result message
     */
    String process(String templateCode, Map<String, Object> variables);
}
