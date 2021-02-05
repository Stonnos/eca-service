package com.ecaservice.mail.service.template;

import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;
import java.util.Map;

/**
 * Template processor interface.
 *
 * @author Roman Batygin
 */
@Validated
public interface TemplateProcessorService {

    /**
     * Creates template based on input parameters.
     *
     * @param templateCode - template code
     * @param variables    - template variables map
     * @return result message
     */
    @NotBlank
    String process(@NotBlank String templateCode, Map<String, String> variables);
}
