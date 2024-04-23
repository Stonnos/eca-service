package com.ecaservice.core.audit.config.rabbit;

import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Audit rabbit mq configuration.
 *
 * @author Roman Batygin
 */
@Configuration
@ConditionalOnClass(CachingConnectionFactory.class)
@ConditionalOnProperty(value = "audit.sender.type", havingValue = "rabbitmq")
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
