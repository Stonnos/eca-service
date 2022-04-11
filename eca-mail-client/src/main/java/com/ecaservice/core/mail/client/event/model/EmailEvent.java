package com.ecaservice.core.mail.client.event.model;

import com.ecaservice.notification.dto.EmailRequest;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * Email event model.
 *
 * @author Roman Batygin
 */
public class EmailEvent extends ApplicationEvent {

    /**
     * Email request dto
     */
    @Getter
    private final EmailRequest emailRequest;

    /**
     * Create a new {@code ApplicationEvent}.
     *
     * @param source       the object on which the event initially occurred or with
     *                     which the event is associated (never {@code null})
     * @param emailRequest - email request
     */
    public EmailEvent(Object source, EmailRequest emailRequest) {
        super(source);
        this.emailRequest = emailRequest;
    }
}
