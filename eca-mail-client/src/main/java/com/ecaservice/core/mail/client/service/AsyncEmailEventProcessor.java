package com.ecaservice.core.mail.client.service;

import com.ecaservice.core.mail.client.event.model.AbstractEmailEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;


/**
 * Asynchronous email event processor.
 *
 * @author Roman Batygin
 */
@Slf4j
@ConditionalOnProperty(value = "mail.client.async", havingValue = "true")
@Primary
@Component
public class AsyncEmailEventProcessor implements EmailEventProcessor {

    private final EmailEventProcessor emailEventProcessor;

    /**
     * Init method.
     */
    @PostConstruct
    public void init() {
        log.info("Email client async processor has been configured");
    }

    /**
     * Constructor with parameters.
     *
     * @param emailEventProcessor - simple email event processor
     */
    public AsyncEmailEventProcessor(@Qualifier("simpleEmailEventProcessor") EmailEventProcessor emailEventProcessor) {
        this.emailEventProcessor = emailEventProcessor;
    }

    @Override
    public void handleEmailEvent(AbstractEmailEvent event) {
        emailEventProcessor.handleEmailEvent(event);
    }
}
