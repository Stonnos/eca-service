package com.ecaservice.service.experiment.mail.template;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

/**
 * Thymeleaf template engine service.
 *
 * @author Roman Batygin
 */
@Service
@RequiredArgsConstructor
public class TemplateEngineService {

    private final TemplateEngine templateEngine;

    /**
     * Processes template with given context.
     *
     * @param template - template path
     * @param context  - context object
     * @return merged content
     */
    public String process(String template, Context context) {
        return templateEngine.process(template, context);
    }
}
