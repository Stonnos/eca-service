package com.ecaservice.core.audit.config.rabbit;

import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Audit rabbit mq configuration.
 *
 * @author Roman Batygin
 */
@Configuration
public class AuditRabbitConfiguration {

    public static final String AUDIT_RABBIT_TEMPLATE = "auditRabbitTemplate";

    /**
     * Creates rabbit template bean.
     *
     * @param connectionFactory - connection factory
     * @return rabbit template bean
     */
    @Bean(AUDIT_RABBIT_TEMPLATE)
    public RabbitTemplate auditRabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());
        return rabbitTemplate;
    }
}
