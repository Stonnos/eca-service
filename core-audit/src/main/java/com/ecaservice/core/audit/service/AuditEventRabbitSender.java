package com.ecaservice.core.audit.service;

import com.ecaservice.audit.dto.AuditEventRequest;
import com.ecaservice.core.audit.config.AuditProperties;
import com.ecaservice.core.redelivery.annotation.Retry;
import com.ecaservice.core.redelivery.annotation.Retryable;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

import static com.ecaservice.core.audit.config.rabbit.AuditRabbitConfiguration.AUDIT_RABBIT_TEMPLATE;

/**
 * Service to sent audit events via rabbitmq.
 *
 * @author Roman Batygin
 */
@Slf4j
@Retryable
@Service
@ConditionalOnProperty(value = "audit.sender.type", havingValue = "rabbitmq")
public class AuditEventRabbitSender implements AuditEventSender {

    private final AuditProperties auditProperties;
    private final RabbitTemplate rabbitTemplate;

    /**
     * Constructor with parameters.
     *
     * @param auditProperties - audit properties
     * @param rabbitTemplate  - rabbit template
     */
    public AuditEventRabbitSender(AuditProperties auditProperties,
                                  @Qualifier(AUDIT_RABBIT_TEMPLATE) RabbitTemplate rabbitTemplate) {
        this.auditProperties = auditProperties;
        this.rabbitTemplate = rabbitTemplate;
    }

    /**
     * Initialization method.
     */
    @PostConstruct
    public void init() {
        log.info("Audit events rabbitmq sender has been configured. Events queue: {}",
                auditProperties.getRabbit().getQueueName());
    }

    @Override
    @Retry(value = "auditRequest", requestIdKey = "#auditEventRequest.eventId")
    public void sendAuditEvent(AuditEventRequest auditEventRequest) {
        String queueName = auditProperties.getRabbit().getQueueName();
        log.info("Starting to send audit event [{}] with code [{}], type [{}] to queue [{}]",
                auditEventRequest.getEventId(), auditEventRequest.getCode(), auditEventRequest.getEventType(),
                queueName);
        rabbitTemplate.convertAndSend(queueName, auditEventRequest);
        log.info("Audit event [{}] with code [{}], type [{}] has been sent to queue [{}]",
                auditEventRequest.getEventId(), auditEventRequest.getCode(), auditEventRequest.getEventType(),
                queueName);
    }
}
